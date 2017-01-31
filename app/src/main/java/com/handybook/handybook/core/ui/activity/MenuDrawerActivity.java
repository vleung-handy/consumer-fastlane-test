package com.handybook.handybook.core.ui.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.handybook.handybook.BuildConfig;
import com.handybook.handybook.R;
import com.handybook.handybook.account.ui.ProfileActivity;
import com.handybook.handybook.booking.history.HistoryActivity;
import com.handybook.handybook.booking.ui.activity.BookingsActivity;
import com.handybook.handybook.booking.ui.activity.ServiceCategoriesActivity;
import com.handybook.handybook.bottomnav.BottomNavActivity;
import com.handybook.handybook.configuration.event.ConfigurationEvent;
import com.handybook.handybook.core.BaseApplication;
import com.handybook.handybook.core.EnvironmentModifier;
import com.handybook.handybook.core.User;
import com.handybook.handybook.core.event.EnvironmentUpdatedEvent;
import com.handybook.handybook.core.event.UserLoggedInEvent;
import com.handybook.handybook.helpcenter.ui.activity.HelpActivity;
import com.handybook.handybook.library.util.Utils;
import com.handybook.handybook.logger.handylogger.LogEvent;
import com.handybook.handybook.logger.handylogger.constants.SourcePage;
import com.handybook.handybook.logger.handylogger.model.ProTeamPageLog;
import com.handybook.handybook.logger.handylogger.model.SideMenuLog;
import com.handybook.handybook.proteam.ui.activity.ProTeamActivity;
import com.handybook.handybook.referral.ui.ReferralActivity;
import com.handybook.shared.layer.LayerHelper;
import com.squareup.otto.Subscribe;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * eventually switch to BottomNavActivity
 */
public abstract class MenuDrawerActivity extends BaseActivity
        implements NavigationView.OnNavigationItemSelectedListener
{
    private static final String TAG = MenuDrawerActivity.class.getName();
    protected static final String EXTRA_SHOW_NAV_FOR_TRANSITION = "EXTRA_SHOW_NAV_FOR_TRANSITION";
    public static final String EXTRA_SHOW_SELECTED_MENU_ITEM = "EXTRA_SHOW_SELECTED_MENU_ITEM";

    @Bind(R.id.drawer_layout)
    protected DrawerLayout mDrawerLayout;

    @Bind(R.id.navigation)
    protected NavigationView mNavigationView;

    @Inject
    EnvironmentModifier mEnvironmentModifier;

    @Inject
    LayerHelper mLayerHelper;

    protected boolean disableDrawer;
    protected boolean mShouldShowNavForTransition;
    private Object mBusEventListener;

    protected abstract Fragment createFragment();

    protected abstract String getNavItemTitle();

    @Override
    protected void onCreate(final Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_drawer);
        ButterKnife.bind(this);
        if (requiresUser() && !mUserManager.isUserLoggedIn())
        {
            navigateToActivity(ServiceCategoriesActivity.class, R.id.nav_menu_home);
            finish();
            return;
        }

        setupEnvButton();
        mNavigationView.setNavigationItemSelectedListener(this);
        int selectedMenuId = getIntent().getIntExtra(EXTRA_SHOW_SELECTED_MENU_ITEM, -1);
        if (selectedMenuId != -1)
        {
            mNavigationView.setCheckedItem(selectedMenuId);
        }
        final FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = fm.findFragmentById(R.id.fragment_container);
        if (fragment == null)
        {
            fragment = createFragment();
            fm.beginTransaction().add(R.id.fragment_container, fragment).commit();
        }
        if (savedInstanceState == null && (mShouldShowNavForTransition
                = getIntent().getBooleanExtra(EXTRA_SHOW_NAV_FOR_TRANSITION, false)))
        {
            mDrawerLayout.closeDrawers();
        }
        //this is to work around the subscriber inheritance issue that Otto has.
        //https://github.com/square/otto/issues/26
        mBusEventListener = new Object()
        {
            @Subscribe
            public void userAuthUpdated(final UserLoggedInEvent event)
            {
                checkLayerInitiation();
                if (!event.isLoggedIn())
                {
                    navigateToActivity(ServiceCategoriesActivity.class, R.id.nav_menu_home);
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
                    checkLayerInitiation();
                    refreshMenu();
                    setDrawerDisabled(mConfigurationManager.getPersistentConfiguration()
                                                           .isBottomNavEnabled());
                }
            }
        };
    }

    /**
     * Layer needs to be initialized under these 2 conditions, and we have to check these conditions
     * on user events (login, logout), and if the config parameter changes
     */
    private void checkLayerInitiation()
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
        //The menu should always be refreshed
        refreshMenu();
    }

    protected boolean requiresUser()
    {
        return false;
    }

    @Override
    protected final void onStart()
    {
        super.onStart();
        if (mShouldShowNavForTransition)
        {
            mDrawerLayout.closeDrawers();
            mShouldShowNavForTransition = false;
        }
    }

    /**
     * Navigates to the activity if it's not already there.
     *
     * @param clazz              Activity class to launch
     * @param menuIdTobeSelected Nav menu item Id to be selected
     */
    public final void navigateToActivity(
            final Class<? extends Activity> clazz,
            @Nullable @IdRes Integer menuIdTobeSelected
    )
    {
        if (this.getClass().getName().equals(clazz.getName()))
        {
            //close drawers and not do anything further
            mDrawerLayout.closeDrawers();
        }
        else
        {
            navigateToActivity(clazz, new Bundle(), menuIdTobeSelected);
        }
    }

    public final void navigateToActivity(
            final Class<? extends Activity> clazz, final Bundle extras,
            @Nullable @IdRes Integer menuIdToBeSelected
    )
    {
        final Intent intent = new Intent(this, clazz);
        intent.putExtras(extras);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(MenuDrawerActivity.EXTRA_SHOW_NAV_FOR_TRANSITION, true);
        if (menuIdToBeSelected != null)
        {
            intent.putExtra(MenuDrawerActivity.EXTRA_SHOW_SELECTED_MENU_ITEM, menuIdToBeSelected);
        }
        startActivity(intent);
        MenuDrawerActivity.this.overridePendingTransition(0, 0);
        MenuDrawerActivity.this.finish();
    }

    public final void setDrawerDisabled(final boolean disableDrawer)
    {
        if (disableDrawer)
        {
            mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        }
        else
        {
            mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
        }
    }

    private void setupEnvButton()
    {
        Button envButton = (Button) mNavigationView.getHeaderView(0).findViewById(R.id.env_button);
        envButton.setText(String.format(
                getString(R.string.env_format),
                mEnvironmentModifier.getEnvironment(),
                BuildConfig.VERSION_NAME,
                Integer.valueOf(BuildConfig.VERSION_CODE).toString()
                          )
        );
        if (BuildConfig.FLAVOR.equals(BaseApplication.FLAVOR_PROD))
        {
            envButton.setVisibility(View.GONE);
        }
        envButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                final EditText input = new EditText(MenuDrawerActivity.this);
                input.setText(mEnvironmentModifier.getEnvironment());
                new AlertDialog.Builder(MenuDrawerActivity.this)
                        .setTitle(R.string.set_environment)
                        .setView(input)
                        .setPositiveButton(R.string.set, new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i)
                            {
                                mEnvironmentModifier.setEnvironment(input.getText().toString());
                            }
                        })
                        .setNegativeButton(R.string.cancel, null)
                        .create()
                        .show();
            }
        });
    }

    /**
     * This is needed for the little hamburger menu icon to animate when opening and closing the
     * drawer
     */
    public void setupHamburgerMenu(Toolbar toolbar)
    {
        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(
                this,
                mDrawerLayout,
                toolbar,
                R.string.open_drawer,
                R.string.close_drawer
        );
        mDrawerLayout.setDrawerListener(actionBarDrawerToggle);
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        mBus.register(mBusEventListener);
        mBus.post(new ConfigurationEvent.RequestConfiguration());
        refreshMenu();
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        mBus.unregister(mBusEventListener);
    }

    /**
     * Updates the menu item visibilities based on the user's login status
     */
    private void refreshMenu()
    {
        final User currentUser = mUserManager.getCurrentUser();
        final boolean userLoggedIn = currentUser != null;

        mNavigationView.getMenu().findItem(R.id.nav_menu_my_bookings).setVisible(userLoggedIn);
        mNavigationView.getMenu().findItem(R.id.nav_menu_profile).setVisible(userLoggedIn);
        mNavigationView.getMenu().findItem(R.id.nav_menu_free_cleanings).setVisible(userLoggedIn);
        mNavigationView.getMenu().findItem(R.id.nav_menu_log_in).setVisible(!userLoggedIn);
        mNavigationView.getMenu().findItem(R.id.nav_menu_my_pro_team).setVisible(userLoggedIn);
        mNavigationView.getMenu().findItem(R.id.nav_menu_history).setVisible(userLoggedIn);
    }

    /**
     * Item selected from the navigation drawer
     */
    @Override
    public boolean onNavigationItemSelected(final MenuItem menuItem)
    {
        switch (menuItem.getItemId())
        {
            case R.id.nav_menu_home:
                if (this instanceof ServiceCategoriesActivity)
                {
                    mDrawerLayout.closeDrawers();
                }
                else
                {
                    navigateToActivity(ServiceCategoriesActivity.class, R.id.nav_menu_home);
                }
                return true;
            case R.id.nav_menu_profile:
                navigateToActivity(ProfileActivity.class, menuItem.getItemId());
                return true;
            case R.id.nav_menu_my_bookings:
                navigateToActivity(BookingsActivity.class, menuItem.getItemId());
                return true;
            case R.id.nav_menu_my_pro_team:
                mBus.post(new LogEvent.AddLogEvent(new ProTeamPageLog.OpenTapped(SourcePage.SIDE_MENU)));
                navigateToActivity(ProTeamActivity.class, menuItem.getItemId());
                return true;
            case R.id.nav_menu_history:
                navigateToActivity(HistoryActivity.class, menuItem.getItemId());
                return true;
            case R.id.nav_menu_free_cleanings:
                mBus.post(new LogEvent.AddLogEvent(new SideMenuLog.ShareButtonTappedLog()));
                navigateToActivity(ReferralActivity.class, menuItem.getItemId());
                return true;
            case R.id.nav_menu_help:
                mBus.post(new LogEvent.AddLogEvent(new SideMenuLog.HelpCenterTappedLog()));
                navigateToActivity(HelpActivity.class, menuItem.getItemId());
                return true;
            case R.id.nav_menu_log_in:
                navigateToActivity(LoginActivity.class, R.id.nav_menu_log_in);
                return false;
        }
        return true;
    }

    @Override
    public void onBackPressed()
    {
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START))
        {
            mDrawerLayout.closeDrawers();
        }
        else if (isTaskRoot() && !(this instanceof ServiceCategoriesActivity)
                && getSupportFragmentManager().getBackStackEntryCount() == 0)
        {
            //if back press results in exiting the app AND this is not the home page
            // AND there is no fragment in the backstack, then bring back to the home page first
            if (mConfigurationManager.getPersistentConfiguration().isBottomNavEnabled())
            {
                startActivity(new Intent(this, BottomNavActivity.class));
                finish();
            }
            else
            {
                navigateToActivity(ServiceCategoriesActivity.class, R.id.nav_menu_home);
            }
        }
        else
        {
            Utils.hideSoftKeyboard(this, getCurrentFocus());
            super.onBackPressed();
        }
    }

    public void toggleMenu()
    {
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START))
        {
            mDrawerLayout.closeDrawers();
        }
        else
        {
            mDrawerLayout.openDrawer(GravityCompat.START);
        }
    }
}
