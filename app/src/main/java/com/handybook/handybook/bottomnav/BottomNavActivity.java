package com.handybook.handybook.bottomnav;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.MenuItem;
import android.view.View;

import com.crashlytics.android.Crashlytics;
import com.handybook.handybook.R;
import com.handybook.handybook.account.ui.AccountFragment;
import com.handybook.handybook.booking.ui.fragment.ServiceCategoriesFragment;
import com.handybook.handybook.booking.ui.fragment.UpcomingBookingsFragment;
import com.handybook.handybook.configuration.event.ConfigurationEvent;
import com.handybook.handybook.configuration.model.Configuration;
import com.handybook.handybook.core.BaseApplication;
import com.handybook.handybook.core.EnvironmentModifier;
import com.handybook.handybook.core.MainNavTab;
import com.handybook.handybook.core.User;
import com.handybook.handybook.core.event.EnvironmentUpdatedEvent;
import com.handybook.handybook.core.event.UserLoggedInEvent;
import com.handybook.handybook.core.ui.activity.BaseActivity;
import com.handybook.handybook.library.util.FragmentUtils;
import com.handybook.handybook.proteam.ui.fragment.ProTeamConversationsFragment;
import com.handybook.handybook.proteam.ui.fragment.ProTeamFragment;
import com.handybook.handybook.referral.ui.ReferralFragment;
import com.handybook.shared.layer.LayerHelper;
import com.squareup.otto.Subscribe;

import java.io.Serializable;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * TODO this is a work in progress and the relevant config should not be turned on in prod yet
 * this should eventually replace the menu drawer activity
 * not bothering to spend time consolidating duplicate code for this reason
 */
public class BottomNavActivity extends BaseActivity implements MainNavTab.Navigator
{
    public static final String BUNDLE_KEY_TAB = "key_tab";

    @Bind(R.id.bottom_navigation)
    BottomNavigationView mBottomNavigationView;

    @Inject
    EnvironmentModifier mEnvironmentModifier;

    @Inject
    LayerHelper mLayerHelper;

    protected Configuration mConfiguration;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bottom_nav);
        ButterKnife.bind(this);

        mBottomNavigationView.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener()
                {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull final MenuItem item)
                    {
                        return onNavItemSelected(item);
                    }
                });
        boolean navigatedToTab = navigateToTabFromBundleExtras();
        if(!navigatedToTab)
        {
            selectDefaultTab();
        }
    }

    /**
     * navigates to the tab given by the bundle extras
     * @return true if navigated to tab from bundle extras
     */
    private boolean navigateToTabFromBundleExtras()
    {
        Bundle extras = getIntent().getExtras();
        if(extras == null) return false;
        Serializable tabToSelect = extras.getSerializable(BUNDLE_KEY_TAB);
        if(tabToSelect != null && tabToSelect instanceof MainNavTab)
        {
            return navigateToMainNavTab((MainNavTab) tabToSelect);
        }
        return false;
    }

    /**
     * TODO refactor to use the navigation methods for consistency?
     */
    private void selectDefaultTab()
    {
        User user = mUserManager.getCurrentUser();
        if (user != null
                && user.getAnalytics() != null
                && user.getAnalytics().getUpcomingBookings() > 0
                && ((BaseApplication) getApplication()).isNewlyLaunched())

        {
            mBottomNavigationView.findViewById(R.id.bookings).performClick();
        }
        else
        {
            mBottomNavigationView.findViewById(R.id.add_booking).performClick();
        }
    }

    @Override
    protected void onNewIntent(final Intent intent)
    {
        super.onNewIntent(intent);
        navigateToMainNavTab(intent);
    }

    @Subscribe
    public void userAuthUpdated(final UserLoggedInEvent event)
    {
        checkLayerInitiation();
        if (!event.isLoggedIn())
        {
            navigateToMainNavTab(MainNavTab.SERVICES);
        }
    }

    @Subscribe
    public void envUpdated(final EnvironmentUpdatedEvent event)
    {
        setupEnvButton();
    }

    @Subscribe
    public void onReceiveConfigurationSuccess(
            final ConfigurationEvent.ReceiveConfigurationSuccess event
    )
    {
        if (event != null)
        {
            mConfiguration = event.getConfiguration();
            checkLayerInitiation();
            refreshMenu();
        }
    }

    @Override
    protected void onResumeFragments()
    {
        super.onResumeFragments();
        mBus.register(this);
        refreshMenu();
    }

    @Override
    protected void onPause()
    {
        mBus.unregister(this);
        super.onPause();
    }

    /**
     * navigate to the bottom nav tab associated with the Tab in the given intent's bundle
     * @param intent
     */
    private void navigateToMainNavTab(Intent intent)
    {
        MainNavTab mainNavTab = (intent == null || intent.getSerializableExtra(BUNDLE_KEY_TAB) == null) ?
                MainNavTab.UNKNOWN : (MainNavTab) intent.getSerializableExtra(BUNDLE_KEY_TAB);
        navigateToMainNavTab(mainNavTab);
    }

    /**
     * navigate to the fragment associated with the given tab
     * mapping menu item -> tab instead of tab -> menu item in case we want to launch a tab whose
     * menu item isn't present
     *
     * @param mainNavTab
     * @return true if navigation was resolved
     */
    @Override
    public boolean navigateToMainNavTab(@NonNull MainNavTab mainNavTab)
    {
        Fragment fragment = null;
        switch (mainNavTab)
        {
            case BOOKINGS:
                fragment = UpcomingBookingsFragment.newInstance();
                break;
            case PRO_TEAM:
                if (mConfigurationManager.getPersistentConfiguration().isChatEnabled())
                {
                    fragment = ProTeamConversationsFragment.newInstance();
                }
                else
                {
                    fragment = ProTeamFragment.newInstance();
                }
                break;
            case SERVICES:
                fragment = ServiceCategoriesFragment.newInstance(null, null);
                break;
            case SHARE:
                fragment = ReferralFragment.newInstance(null);
                break;
            case ACCOUNT:
                fragment = AccountFragment.newInstance();
                break;
            default:
                Crashlytics.logException(new Exception("Don't know how to handle navigation for the given tab: " + mainNavTab
                        .toString()));
                break;
        }
        if (fragment != null)
        {
            FragmentUtils.switchToFragment(BottomNavActivity.this, fragment, false);
            return true;
        }
        else
        {
            return false;
        }
    }

    private boolean navigateToMainNavTab(@NonNull MenuItem menuItem)
    {
        MainNavTab mainNavTab = null;
        switch (menuItem.getItemId())
        {
            case R.id.bookings:
                mainNavTab = MainNavTab.BOOKINGS;
                break;
            case R.id.pro_team:
                mainNavTab = MainNavTab.PRO_TEAM;
                break;
            case R.id.add_booking:
                mainNavTab = MainNavTab.SERVICES;
                break;
            case R.id.gift:
                mainNavTab = MainNavTab.SHARE;
                break;
            case R.id.account:
                mainNavTab = MainNavTab.ACCOUNT;
                break;
        }
        if(mainNavTab == null)
        {
            Crashlytics.logException(new Exception("Unable to navigate to tab for menu item " + menuItem.getItemId()));
            return false;
        }
        return navigateToMainNavTab(mainNavTab);
    }

    //TODO probably consolidate this
    private boolean onNavItemSelected(@NonNull final MenuItem item)
    {
        return navigateToMainNavTab(item);
    }

    /**
     * Layer needs to be initialized under these 2 conditions, and we have to check these conditions
     * on user events (login, logout), and if the config parameter changes
     */
    private void checkLayerInitiation()
    {
        if (mConfiguration == null || !mConfiguration.isChatEnabled())
        {
            //Layer should be disabled.
            mLayerHelper.deauthenticate();
        }
        else
        {
            //chat is enabled, so we'll login if the user is available
            User user = mUserManager.getCurrentUser();

            if (user != null)
            {
                mLayerHelper.initLayer(user.getAuthToken());
            }
            else
            {
                //the user is in a logged out state
                mLayerHelper.deauthenticate();
            }
        }
        //The menu should always be refreshed
        refreshMenu();
    }

    /**
     * Updates the menu item visibilities based on the user's login status
     */
    private void refreshMenu()
    {
        final User currentUser = mUserManager.getCurrentUser();
        final boolean userLoggedIn = currentUser != null;

        mBottomNavigationView.setVisibility(userLoggedIn ? View.VISIBLE : View.GONE);
    }

    protected boolean requiresUser()
    {
        return false;
    }

    //TODO need to ask where env button should be placed
    private void setupEnvButton()
    {
//        Button envButton = (Button) mNavigationView.getHeaderView(0).findViewById(R.id.env_button);
//        envButton.setText(String.format(
//                getString(R.string.env_format),
//                mEnvironmentModifier.getEnvironment(),
//                BuildConfig.VERSION_NAME,
//                Integer.valueOf(BuildConfig.VERSION_CODE).toString()
//                          )
//        );
//        if (BuildConfig.FLAVOR.equals(BaseApplication.FLAVOR_PROD))
//        {
//            envButton.setVisibility(View.GONE);
//        }
//        envButton.setOnClickListener(new View.OnClickListener()
//        {
//            @Override
//            public void onClick(View view)
//            {
//                final EditText input = new EditText(BottomNavActivity.this);
//                input.setText(mEnvironmentModifier.getEnvironment());
//                new AlertDialog.Builder(BottomNavActivity.this)
//                        .setTitle(R.string.set_environment)
//                        .setView(input)
//                        .setPositiveButton(R.string.set, new DialogInterface.OnClickListener()
//                        {
//                            @Override
//                            public void onClick(DialogInterface dialogInterface, int i)
//                            {
//                                mEnvironmentModifier.setEnvironment(input.getText().toString());
//                            }
//                        })
//                        .setNegativeButton(R.string.cancel, null)
//                        .create()
//                        .show();
//            }
//        });
    }
}
