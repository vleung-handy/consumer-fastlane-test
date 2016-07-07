package com.handybook.handybook.ui.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.handybook.handybook.BuildConfig;
import com.handybook.handybook.R;
import com.handybook.handybook.booking.ui.activity.BookingsActivity;
import com.handybook.handybook.booking.ui.activity.PromosActivity;
import com.handybook.handybook.booking.ui.activity.ServiceCategoriesActivity;
import com.handybook.handybook.core.BaseApplication;
import com.handybook.handybook.core.EnvironmentModifier;
import com.handybook.handybook.core.User;
import com.handybook.handybook.event.EnvironmentUpdatedEvent;
import com.handybook.handybook.event.UserLoggedInEvent;
import com.handybook.handybook.helpcenter.ui.activity.HelpActivity;
import com.handybook.handybook.module.configuration.event.ConfigurationEvent;
import com.handybook.handybook.module.configuration.model.Configuration;
import com.handybook.handybook.module.proteam.ui.activity.ProTeamActivity;
import com.handybook.handybook.module.referral.ui.ReferralActivity;
import com.squareup.otto.Subscribe;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;

public abstract class MenuDrawerActivity extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener
{
    private static final String TAG = MenuDrawerActivity.class.getName();
    private static final String EXTRA_SHOW_NAV_FOR_TRANSITION = "EXTRA_SHOW_NAV_FOR_TRANSITION";
    private static final String EXTRA_SHOW_SELECTED_MENU_ITEM = "EXTRA_SHOW_SELECTED_MENU_ITEM";

    @Bind(R.id.drawer_layout)
    DrawerLayout mDrawerLayout;

    @Bind(R.id.navigation)
    NavigationView mNavigationView;
    @Inject
    EnvironmentModifier mEnvironmentModifier;

    protected boolean disableDrawer;
    protected Configuration mConfiguration;
    private boolean mShouldShowNavForTransition;
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
            navigateToActivity(ServiceCategoriesActivity.class);
            finish();
            return;
        }
        FacebookSdk.sdkInitialize(getApplicationContext());
        setupEnvButton();
        int selectedMenuId = getIntent().getIntExtra(EXTRA_SHOW_SELECTED_MENU_ITEM, -1);
        mNavigationView.setCheckedItem(selectedMenuId);
        mNavigationView.setNavigationItemSelectedListener(this);
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
                refreshMenu();
                if (!event.isLoggedIn())
                {
                    navigateToActivity(ServiceCategoriesActivity.class);
                }
            }

            @Subscribe
            public void envUpdated(final EnvironmentUpdatedEvent event)
            {
                Log.d(TAG, "received EnvironmentUpdatedEvent");
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
                    refreshMenu();
                }
            }
        };
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

    public final void navigateToActivity(final Class<? extends Activity> clazz)
    {
        navigateToActivity(clazz, new Bundle());
    }

    /**
     * Navigates to the activity if it's not already there.
     *
     * @param clazz
     * @param menuIdToBeSelected
     */
    public final void navigateToActivity(final Class<? extends Activity> clazz, int menuIdToBeSelected)
    {
        if (this.getClass().getName().equals(clazz.getName()))
        {
            //close drawers and not do anything further
            mDrawerLayout.closeDrawers();
        }
        else
        {
            Bundle bundle = new Bundle();
            bundle.putInt(MenuDrawerActivity.EXTRA_SHOW_SELECTED_MENU_ITEM, menuIdToBeSelected);
            navigateToActivity(clazz, bundle);
        }
    }


    public final void navigateToActivity(final Class<? extends Activity> clazz, final Bundle extras)
    {
        final Intent intent = new Intent(this, clazz);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(MenuDrawerActivity.EXTRA_SHOW_NAV_FOR_TRANSITION, true);
        intent.putExtras(extras);
        startActivity(intent);
        MenuDrawerActivity.this.overridePendingTransition(0, 0);
        MenuDrawerActivity.this.finish();
    }

    public final void setDrawerDisabled(final boolean disableDrawer)
    {
        this.disableDrawer = disableDrawer;
    }

    private void setupEnvButton()
    {
        Button envButton = (Button) mNavigationView.getHeaderView(0).findViewById(R.id.env_button);
        envButton.setText(String.format(
                getString(R.string.env_format),
                mEnvironmentModifier.getEnvironment(),
                BuildConfig.VERSION_NAME,
                Integer.valueOf(BuildConfig.VERSION_CODE).toString())
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
     * This is needed for the little hamburger menu icon to animate when opening and closing the drawer
     */
    public void setupHamburgerMenu(Toolbar toolbar)
    {
        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, toolbar, R.string.open_drawer, R.string.close_drawer);
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
        mNavigationView.getMenu().findItem(R.id.nav_menu_log_out).setVisible(userLoggedIn);
        mNavigationView.getMenu().findItem(R.id.nav_menu_log_in).setVisible(!userLoggedIn);

        mNavigationView.getMenu().findItem(R.id.nav_menu_payment).setVisible(currentUser != null && currentUser.getStripeKey() != null);

        if (mConfiguration != null)
        {
            mNavigationView.getMenu().findItem(R.id.nav_menu_my_pro_team).setVisible(mConfiguration.isMyProTeamEnabled());
        }
    }

    /**
     * Item selected from the navigation drawer
     *
     * @param menuItem
     * @return
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
                    navigateToActivity(ServiceCategoriesActivity.class);
                }
                return true;
            case R.id.nav_menu_profile:
                navigateToActivity(ProfileActivity.class, menuItem.getItemId());
                return true;
            case R.id.nav_menu_my_bookings:
                navigateToActivity(BookingsActivity.class, menuItem.getItemId());
                return true;
            case R.id.nav_menu_my_pro_team:
                navigateToActivity(ProTeamActivity.class, menuItem.getItemId());
                return true;
            case R.id.nav_menu_payment:
                navigateToActivity(UpdatePaymentActivity.class, menuItem.getItemId());
                return true;
            case R.id.nav_menu_free_cleanings:
                navigateToActivity(ReferralActivity.class, menuItem.getItemId());
                return true;
            case R.id.nav_menu_help:
                navigateToActivity(HelpActivity.class, menuItem.getItemId());
                return true;
            case R.id.nav_menu_promotions:
                navigateToActivity(PromosActivity.class, menuItem.getItemId());
                return true;
            case R.id.nav_menu_log_out:
                mDrawerLayout.closeDrawers();
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(MenuDrawerActivity.this)
                        .setMessage(R.string.want_to_log_out)
                        .setPositiveButton(R.string.log_out, new DialogInterface.OnClickListener()
                        {
                            public void onClick(DialogInterface dialog, int which)
                            {
                                mUserManager.setCurrentUser(null);
                                //log out of Facebook also
                                LoginManager.getInstance().logOut();
                            }
                        })
                        .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener()
                        {
                            public void onClick(DialogInterface dialog, int which)
                            {
                                //do nothing if it's canceled
                            }
                        });

                alertDialog.show();

                return false;
            case R.id.nav_menu_log_in:
                navigateToActivity(LoginActivity.class);
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
        else if (isTaskRoot() && !(this instanceof ServiceCategoriesActivity))
        {
            //if backpress results in exiting the app AND this is not the home page, then bring back to the home page first
            navigateToActivity(ServiceCategoriesActivity.class);
        }
        else
        {
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
