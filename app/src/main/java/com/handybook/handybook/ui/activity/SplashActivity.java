package com.handybook.handybook.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;

import com.facebook.FacebookSdk;
import com.handybook.handybook.R;
import com.handybook.handybook.booking.ui.activity.BookingsActivity;
import com.handybook.handybook.booking.ui.activity.ServiceCategoriesActivity;
import com.handybook.handybook.constant.PrefsKey;
import com.handybook.handybook.core.BaseApplication;
import com.handybook.handybook.core.User;
import com.handybook.handybook.core.UserManager;
import com.handybook.handybook.logger.handylogger.LogEvent;
import com.handybook.handybook.logger.handylogger.model.AppLog;
import com.handybook.handybook.manager.SecurePreferencesManager;
import com.handybook.handybook.module.configuration.event.ConfigurationEvent;
import com.handybook.handybook.module.notifications.splash.model.SplashPromo;
import com.handybook.handybook.module.referral.manager.ReferralsManager;
import com.handybook.handybook.module.referral.model.ReferralResponse;
import com.squareup.otto.Subscribe;
import com.usebutton.sdk.Button;

import javax.inject.Inject;

/**
 * Created by sng on 10/18/16. This is the first activity that gets hit on start up.
 */

public class SplashActivity extends BaseActivity
{
    private static final String TAG = SplashActivity.class.getSimpleName();
    @Inject
    SecurePreferencesManager mSecurePreferencesManager;
    @Inject
    protected UserManager userManager;
    private Object mBusEventListener;
    private Object mBusErrorEventListener;

    @Override
    protected void onCreate(final Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        ((BaseApplication) getApplication()).inject(this);

        //check if this is first launch
        if (mSecurePreferencesManager.getBoolean(PrefsKey.APP_FIRST_LAUNCH, true))
        {
            mBus.post(new LogEvent.AddLogEvent(new AppLog.AppOpenLog(true, true)));
            mSecurePreferencesManager.setBoolean(PrefsKey.APP_FIRST_LAUNCH, false);
        }
        else
        {
            mBus.post(new LogEvent.AddLogEvent(new AppLog.AppOpenLog(false, true)));
        }

        //This is used to check for referral deeplinks
        Button.checkForDeepLink(this, new Button.DeepLinkListener()
        {
            @Override
            public void onDeepLink(final Intent intent)
            {
                startActivity(intent);
            }

            @Override
            public void onNoDeepLink()
            {

            }
        });

        final User user = userManager.getCurrentUser();
        if (!mDefaultPreferencesManager.getBoolean(
                PrefsKey.APP_ONBOARD_SHOWN,
                false
        ) && user == null)
        {
            final Intent intent = new Intent(this, OnboardActivity.class);
            startActivity(intent);
            finish();
        }
        else if (user != null
                && user.getAnalytics() != null
                && user.getAnalytics().getUpcomingBookings() > 0
                && ((BaseApplication) getApplication()).isNewlyLaunched())
        {
            final Intent intent = new Intent(this, BookingsActivity.class);
            startActivity(intent);
            finish();
        }
        else
        {
            //this is to work around the subscriber inheritance issue that Otto has.
            //https://github.com/square/otto/issues/26
            mBusEventListener = new Object()
            {
                @Subscribe
                public void onReceiveConfigurationSuccess(
                        final ConfigurationEvent.ReceiveConfigurationSuccess event
                )
                {
                    if (event != null)
                    {
                        final Intent intent = ServiceCategoriesActivity.getIntent(
                                SplashActivity.this,
                                getIntent()
                        );
                        startActivity(intent);
                        finish();
                    }
                }
            };

            mBusErrorEventListener = new Object()
            {
                @Subscribe
                public void onReceiveConfigurationError(ConfigurationEvent.ReceiveConfigurationError event)
                {
                    if (event != null)
                    {
                        final Intent intent = ServiceCategoriesActivity.getIntent(
                                SplashActivity.this,
                                getIntent()
                        );
                        startActivity(intent);
                        finish();
                    }
                }
            };
        }
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        if (mBusEventListener != null) { mBus.register(mBusEventListener); }
        if (mBusErrorEventListener != null) { mBus.register(mBusErrorEventListener); }
        mBus.post(new ConfigurationEvent.RequestConfiguration());
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        if (mBusEventListener != null) { mBus.unregister(mBusEventListener); }
        if (mBusErrorEventListener != null) { mBus.unregister(mBusErrorEventListener); }
    }

    @Override
    public void showSplashPromo(@NonNull final SplashPromo splashPromo)
    {
        //Splash Activity doesn't need this. Do nothing.
        //Splash promo is really used for Mobile promo pages on the main section
    }

    @Override
    public void showBlockingScreen()
    {
        //Splash Activity doesn't need this. Do nothing.
    }

    @Override
    public void showReferralDialog(
            final ReferralResponse referralResponse, final ReferralsManager.Source source
    )
    {
        //Splash Activity doesn't need this. Do nothing.
    }
}
