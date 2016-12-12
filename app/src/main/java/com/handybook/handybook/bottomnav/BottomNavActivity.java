package com.handybook.handybook.bottomnav;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.MenuItem;
import android.view.View;

import com.handybook.handybook.R;
import com.handybook.handybook.account.ui.AccountFragment;
import com.handybook.handybook.booking.ui.fragment.ServiceCategoriesFragment;
import com.handybook.handybook.booking.ui.fragment.UpcomingBookingsFragment;
import com.handybook.handybook.configuration.event.ConfigurationEvent;
import com.handybook.handybook.configuration.model.Configuration;
import com.handybook.handybook.core.BaseApplication;
import com.handybook.handybook.core.EnvironmentModifier;
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

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * this should eventually replace the menu drawer activity
 * not bothering to spend time consolidating duplicate code for this reason
 */
public class BottomNavActivity extends BaseActivity
{
    public static final String BUNDLE_KEY_TAB = "key_tab";

    @Bind(R.id.bottom_navigation)
    BottomNavigationView mBottomNavigationView;

    @Inject
    EnvironmentModifier mEnvironmentModifier;

    @Inject
    LayerHelper mLayerHelper;

    protected boolean disableDrawer;
    protected Configuration mConfiguration;
    private Object mBusEventListener;

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

        //TODO copied from MenuDrawerActivity - this should be refactored
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
                    navigateToMenuItem(R.id.add_booking);
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
        };
        //TODO refactor
    }

    @Override
    protected void onResumeFragments()
    {
        super.onResumeFragments();
        mBus.register(mBusEventListener);
        refreshMenu();
    }

    @Override
    protected void onPause()
    {
        mBus.unregister(mBusEventListener);
        super.onPause();
    }

    private boolean navigateToMenuItem(int menuItemId)
    {
        Fragment fragment = null;
        switch (menuItemId)
        {
            case R.id.bookings:
                fragment = UpcomingBookingsFragment.newInstance();
                break;
            case R.id.pro_team:
                if (mConfigurationManager.getPersistentConfiguration().isChatEnabled())
                {
                    fragment = ProTeamConversationsFragment.newInstance();
                }
                else
                {
                    fragment = ProTeamFragment.newInstance();
                }
                break;
            case R.id.add_booking:
                fragment = ServiceCategoriesFragment.newInstance(null, null);
                break;
            case R.id.gift:
                fragment = ReferralFragment.newInstance(null);
                break;
            case R.id.account:
                fragment = AccountFragment.newInstance();
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

    //TODO probably consolidate this
    private boolean onNavItemSelected(@NonNull final MenuItem item)
    {
        return navigateToMenuItem(item.getItemId());
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
//        Menu menu = mBottomNavigationView.getMenu();
//        menu.getItem(R.id.bookings).setVisible(userLoggedIn);
//        menu.getItem(R.id.pro_team).setVisible(userLoggedIn);
//        menu.getItem(R.id.add_booking).setVisible(true);
//        menu.getItem(R.id.gift).setVisible(userLoggedIn);
//        menu.getItem(R.id.account).setVisible(userLoggedIn);
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
