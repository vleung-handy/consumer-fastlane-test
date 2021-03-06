package com.handybook.handybook.core.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;
import android.text.TextUtils;

import com.handybook.handybook.R;
import com.handybook.handybook.booking.ui.activity.ServiceCategoriesActivity;
import com.handybook.handybook.bottomnav.BottomNavActivity;
import com.handybook.handybook.configuration.event.ConfigurationEvent;
import com.handybook.handybook.configuration.model.Configuration;
import com.handybook.handybook.core.BaseApplication;
import com.handybook.handybook.core.User;
import com.handybook.handybook.core.UserManager;
import com.handybook.handybook.core.constant.PrefsKey;
import com.handybook.handybook.core.manager.SecurePreferencesManager;
import com.handybook.handybook.logger.handylogger.LogEvent;
import com.handybook.handybook.logger.handylogger.model.AppLog;
import com.handybook.handybook.onboarding.OnboardActivity;
import com.handybook.handybook.promos.splash.SplashPromo;
import com.handybook.handybook.referral.manager.ReferralsManager;
import com.handybook.handybook.referral.model.ReferralResponse;
import com.usebutton.sdk.Button;

import org.greenrobot.eventbus.Subscribe;

import javax.inject.Inject;

/**
 * Created by sng on 10/18/16. This is the first activity that gets hit on start up.
 */
public class SplashActivity extends BaseActivity {

    @Inject
    SecurePreferencesManager mSecurePreferencesManager;
    @Inject
    protected UserManager userManager;
    private Object mBusEventListener;
    private Object mBusErrorEventListener;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        ((BaseApplication) getApplication()).inject(this);

        //check if this is first launch
        if (mSecurePreferencesManager.getBoolean(PrefsKey.APP_FIRST_LAUNCH, true)) {
            mBus.post(new LogEvent.AddLogEvent(new AppLog.AppOpenLog(true, true)));
            mSecurePreferencesManager.setBoolean(PrefsKey.APP_FIRST_LAUNCH, false);
        }
        else {
            mBus.post(new LogEvent.AddLogEvent(new AppLog.AppOpenLog(false, true)));
        }

        //This is used to check for referral deeplinks
        Button.checkForDeepLink(this, new Button.DeepLinkListener() {
            @Override
            public void onDeepLink(final Intent intent) {
                startActivity(intent);
            }

            @Override
            public void onNoDeepLink() {

            }
        });

        //this is to work around the subscriber inheritance issue that Otto has.
        //https://github.com/square/otto/issues/26
        mBusEventListener = new Object() {
            @Subscribe
            public void onReceiveConfigurationSuccess(
                    final ConfigurationEvent.ReceiveConfigurationSuccess event
            ) {
                if (event != null) {
                    goToStartPage(event.getConfiguration());
                }
                else {
                    navigateToServiceCategoriesActivity();
                    unregisterAndFinish();
                }
            }
        };

        mBusErrorEventListener = new Object() {
            @Subscribe
            public void onReceiveConfigurationError(ConfigurationEvent.ReceiveConfigurationError event) {
                goToStartPage(mConfigurationManager.getPersistentConfiguration());
            }
        };

        mBus.register(mBusEventListener);
        mBus.register(mBusErrorEventListener);
        mBus.post(new ConfigurationEvent.RequestConfiguration());

    }

    /**
     * After we have a config, we use that information to decide where to go next (especially
     * for onboarding and bottom nav)
     * @param config
     */
    @VisibleForTesting
    public void goToStartPage(Configuration config) {
        final User user = userManager.getCurrentUser();

        if (user != null) {
            //TODO investigate  <-- @Xi what is it that we need to investigate?

            //This needs to be set to fix an issue with existing users not having their zip set
            if (config.isSaveZipCodeEnabled() &&
                TextUtils.isEmpty(mDefaultPreferencesManager.getString(PrefsKey.ZIP, null)) &&
                user.getAddress() != null &&
                !TextUtils.isEmpty(user.getAddress().getZip())) {
                //the fact that the user is logged in guarantees at least zip information
                mDefaultPreferencesManager.setString(PrefsKey.ZIP, user.getAddress().getZip());
            }

            final Intent intent = new Intent(this, BottomNavActivity.class);
            startActivity(intent);
        }
        //if onboarding is enabled, and we haven't collected email and zip yet, then show the onboarding page
        else if (requiresOnboardingV2(config) ||
                 !mDefaultPreferencesManager.getBoolean(PrefsKey.APP_ONBOARD_SHOWN, false)) {
            startActivity(new Intent(this, OnboardActivity.class));
        }
        else {
            //all else, go to home page
            navigateToServiceCategoriesActivity();
        }
        //At the end always unregister and finish
        unregisterAndFinish();
    }

    /**
     * After we did what we need to do, we no longer care about bus events.
     */
    private void unregisterAndFinish() {
        if (mBusEventListener != null) { mBus.unregister(mBusEventListener); }
        if (mBusErrorEventListener != null) { mBus.unregister(mBusErrorEventListener); }
        finish();
    }

    private void navigateToServiceCategoriesActivity() {
        final Intent intent = ServiceCategoriesActivity.getIntent(
                this,
                getIntent()
        );
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        //don't need history when launching this activity from splash
        startActivity(intent);
    }

    @Override
    public void showSplashPromo(@NonNull final SplashPromo splashPromo) {
        //Splash Activity doesn't need this. Do nothing.
        //Splash promo is really used for Mobile promo pages on the main section
    }

    @Override
    public void showBlockingScreen() {
        //Splash Activity doesn't need this. Do nothing.
    }

    @Override
    public void showReferralDialog(
            final ReferralResponse referralResponse, final ReferralsManager.Source source
    ) {
        //Splash Activity doesn't need this. Do nothing.
    }
}
