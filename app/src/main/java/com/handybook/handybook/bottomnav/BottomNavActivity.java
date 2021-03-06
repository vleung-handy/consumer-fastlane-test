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
import com.handybook.handybook.booking.ui.fragment.ServiceCategoriesHomeFragment;
import com.handybook.handybook.booking.ui.fragment.UpcomingAndPastBookingsFragment;
import com.handybook.handybook.booking.ui.fragment.UpcomingBookingsFragment;
import com.handybook.handybook.configuration.event.ConfigurationEvent;
import com.handybook.handybook.core.EnvironmentModifier;
import com.handybook.handybook.core.MainNavTab;
import com.handybook.handybook.core.User;
import com.handybook.handybook.core.ui.activity.BaseActivity;
import com.handybook.handybook.library.util.FragmentUtils;
import com.handybook.handybook.proteam.mypros.MyProsFragment;
import com.handybook.handybook.proteam.ui.fragment.ProTeamConversationsFragment;
import com.handybook.handybook.referral.ui.ReferralV2Fragment;
import com.handybook.shared.layer.LayerConstants;
import com.handybook.shared.layer.LayerHelper;

import org.greenrobot.eventbus.Subscribe;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * this should eventually replace the menu drawer activity
 * not bothering to spend time consolidating duplicate code for this reason
 */
public class BottomNavActivity extends BaseActivity {

    public static final String BUNDLE_KEY_TAB = "key_tab";

    @BindView(R.id.bottom_navigation)
    BottomNavigationView mBottomNavigationView;

    @Inject
    EnvironmentModifier mEnvironmentModifier;

    @Inject
    LayerHelper mLayerHelper;

    private BroadcastReceiver mChatNotificationReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bottom_nav);
        ButterKnife.bind(this);

        if (mConfigurationManager.getPersistentConfiguration().isMyProsTabEnabled()) {
            MenuItem menuItem = mBottomNavigationView.getMenu().findItem(R.id.messages);
            menuItem.setTitle(R.string.my_pros_tab_title);
            menuItem.setIcon(R.drawable.ic_menu_my_pros);

            /*
            note: could not simply add another menu item that is hidden because
            bottom nav menu view only supports max of 5 items
             */
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
                if (mBottomNavigationView != null) {
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
        mBottomNavigationView.showChatIndicator(mLayerHelper.getUnreadConversationsCount() > 0);
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
                if (mConfigurationManager.getPersistentConfiguration()
                                         .isUpcomingAndPastBookingsMergeEnabled()) {
                    fragment = UpcomingAndPastBookingsFragment.newInstance();
                }
                else {
                    fragment = UpcomingBookingsFragment.newInstance();
                }
                break;
            case R.id.messages:
                if (mConfigurationManager.getPersistentConfiguration().isMyProsTabEnabled()) {
                    fragment = MyProsFragment.newInstance();
                }
                else {
                    fragment = ProTeamConversationsFragment.newInstance();
                }
                break;
            case R.id.add_booking:
                fragment = ServiceCategoriesHomeFragment.newInstance();
                break;
            case R.id.gift:
                fragment = ReferralV2Fragment.newInstance(null);
                break;
            case R.id.account:
                fragment = AccountFragment.newInstance();
                break;
        }

        if (fragment == null) { return false; }

        FragmentUtils.switchToFragment(BottomNavActivity.this, fragment, false);

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
