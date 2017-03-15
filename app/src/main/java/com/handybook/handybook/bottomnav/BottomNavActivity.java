package com.handybook.handybook.bottomnav;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.MenuItem;
import android.view.View;

import com.handybook.handybook.R;
import com.handybook.handybook.account.ui.AccountFragment;
import com.handybook.handybook.booking.ui.fragment.ServiceCategoriesFragment;
import com.handybook.handybook.booking.ui.fragment.ServiceCategoriesHomeFragment;
import com.handybook.handybook.booking.ui.fragment.UpcomingBookingsFragment;
import com.handybook.handybook.configuration.event.ConfigurationEvent;
import com.handybook.handybook.core.EnvironmentModifier;
import com.handybook.handybook.core.MainNavTab;
import com.handybook.handybook.core.User;
import com.handybook.handybook.core.constant.BundleKeys;
import com.handybook.handybook.core.ui.activity.BaseActivity;
import com.handybook.handybook.library.util.FragmentUtils;
import com.handybook.handybook.proteam.ui.fragment.ProTeamConversationsFragment;
import com.handybook.handybook.referral.ui.ReferralFragment;
import com.handybook.handybook.referral.ui.ReferralV2Fragment;
import com.handybook.shared.layer.LayerConstants;
import com.handybook.shared.layer.LayerHelper;
import com.squareup.otto.Subscribe;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * this should eventually replace the menu drawer activity
 * not bothering to spend time consolidating duplicate code for this reason
 */
public class BottomNavActivity extends BaseActivity {

    public static final String BUNDLE_KEY_TAB = "key_tab";

    @Bind(R.id.bottom_navigation)
    BottomNavigationView mBottomNavigationView;

    @Inject
    EnvironmentModifier mEnvironmentModifier;

    @Inject
    LayerHelper mLayerHelper;

    private BroadcastReceiver mChatNotificationReceiver;

    //This is used for the Handy pro chat indicator
    private boolean isProChatCurrentlySelected;

    // This is used to select the right tab when coming back from saveInstanceState
    @IdRes
    private int mCurrentSelectedTabId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bottom_nav);
        ButterKnife.bind(this);

        // Currently, performClick() is the only way to select the menu item for now. MenuItem.setChecked() doesn't work as expected.
        // We must call performClick() before setOnNavigationItemSelectedListener() so it only highlights the menu item.
        if (savedInstanceState != null) {
            mCurrentSelectedTabId = savedInstanceState.getInt(BundleKeys.TAB_ID, -1);
            View view = mBottomNavigationView.findViewById(mCurrentSelectedTabId);
            if (view != null) {
                view.performClick();
            }
        }

        mBottomNavigationView.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull final MenuItem item) {
                        return onTabItemSelected(item);
                    }
                });

        // We don't need to open a new fragment on coming back from saveInstanceState.
        if (savedInstanceState == null) {
            navigateToMainNavTab(getIntent());
        }

        mChatNotificationReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(final Context context, final Intent intent) {
                if (!isProChatCurrentlySelected && mBottomNavigationView != null) {
                    mBottomNavigationView.showChatIndicator(true);
                }
            }
        };
    }

    @Override
    protected void onNewIntent(final Intent intent) {
        super.onNewIntent(intent);
        navigateToMainNavTab(intent);
    }

    @Subscribe
    public void onReceiveConfigurationSuccess(
            final ConfigurationEvent.ReceiveConfigurationSuccess event
    ) {
        if (event != null) {
            checkLayerInitiation();
            refreshMenu();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerChatNotificationReceiver();
    }

    @Override
    protected void onResumeFragments() {
        super.onResumeFragments();
        mBus.register(this);
        refreshMenu();
    }

    @Override
    protected void onPause() {
        mBus.unregister(this);
        unregisterReceiver(mChatNotificationReceiver);
        super.onPause();
    }

    @Override
    protected void onSaveInstanceState(final Bundle outState) {
        // Saving the id of current selected bottom tab.
        outState.putInt(BundleKeys.TAB_ID, mCurrentSelectedTabId);
        super.onSaveInstanceState(outState);
    }

    private void navigateToMainNavTab(@Nullable Intent intent) {
        MainNavTab tab = (intent == null || intent.getSerializableExtra(BUNDLE_KEY_TAB) == null)
                         ?
                         MainNavTab.UNKNOWN
                         : (MainNavTab) intent.getSerializableExtra(BUNDLE_KEY_TAB);

        switch (tab) {
            case BOOKINGS:
                goToSelectedTab(R.id.bookings);
                break;
            case MESSAGES:
                goToSelectedTab(R.id.messages);
                break;
            case SERVICES:
                goToSelectedTab(R.id.add_booking);
                break;
            case SHARE:
                goToSelectedTab(R.id.gift);
                break;
            case ACCOUNT:
                goToSelectedTab(R.id.account);
                break;
            default:
                User user = mUserManager.getCurrentUser();
                if (user != null && user.getAnalytics() != null
                    && user.getAnalytics().getUpcomingBookings() > 0) {
                    goToSelectedTab(R.id.bookings);
                }
                else {
                    goToSelectedTab(R.id.add_booking);
                }
        }
    }

    private void goToSelectedTab(@IdRes int id) {
        mBottomNavigationView.findViewById(id).performClick();
    }

    private boolean onTabItemSelected(@NonNull final MenuItem item) {
        Fragment fragment = null;
        switch (item.getItemId()) {
            case R.id.bookings:
                fragment = UpcomingBookingsFragment.newInstance();
                break;
            case R.id.messages:
                isProChatCurrentlySelected = true;
                mBottomNavigationView.showChatIndicator(false);
                fragment = ProTeamConversationsFragment.newInstance();
                break;
            case R.id.add_booking:
                if (mConfigurationManager.getPersistentConfiguration().isHomeScreenV2Enabled()) {
                    fragment = ServiceCategoriesHomeFragment.newInstance();
                }
                else {
                    fragment = ServiceCategoriesFragment.newInstance(null, null);
                }
                break;
            case R.id.gift:
                if (mConfigurationManager.getPersistentConfiguration().isShareProEnabled()) {
                    fragment = new ReferralV2Fragment();
                }
                else {
                    fragment = ReferralFragment.newInstance(null, null, false);
                }
                break;
            case R.id.account:
                fragment = AccountFragment.newInstance();
                break;
        }

        if (fragment == null) { return false; }

        FragmentUtils.switchToFragment(BottomNavActivity.this, fragment, false);
        mCurrentSelectedTabId = item.getItemId();

        return true;
    }

    /**
     * Layer needs to be initialized under these 2 conditions, and we have to check these conditions
     * on user events (login, logout), and if the config parameter changes
     */
    private void checkLayerInitiation() {
        //chat is enabled, so we'll login if the user is available
        User user = mUserManager.getCurrentUser();

        if (user != null) {
            mLayerHelper.initLayer(user.getAuthToken());
        }
        else {
            //the user is in a logged out state
            mLayerHelper.deauthenticate();
        }
        //The menu should always be refreshed
        refreshMenu();
    }

    /**
     * Updates the menu item visibilities based on the user's login status
     */
    private void refreshMenu() {
        final User currentUser = mUserManager.getCurrentUser();
        final boolean userLoggedIn = currentUser != null;

        mBottomNavigationView.setVisibility(userLoggedIn ? View.VISIBLE : View.GONE);
    }

    private void registerChatNotificationReceiver() {
        final IntentFilter filter = new IntentFilter(LayerConstants.ACTION_SHOW_NOTIFICATION);
        filter.setPriority(IntentFilter.SYSTEM_HIGH_PRIORITY);
        registerReceiver(mChatNotificationReceiver, filter);
    }
}
