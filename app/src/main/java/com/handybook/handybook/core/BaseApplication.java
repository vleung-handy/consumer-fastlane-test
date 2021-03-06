package com.handybook.handybook.core;

import android.app.Activity;
import android.os.Bundle;
import android.provider.Settings;
import android.support.multidex.MultiDexApplication;
import android.support.v4.content.ContextCompat;

import com.crashlytics.android.Crashlytics;
import com.facebook.FacebookSdk;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.handybook.handybook.BuildConfig;
import com.handybook.handybook.R;
import com.handybook.handybook.booking.bookingedit.manager.BookingEditManager;
import com.handybook.handybook.booking.manager.BookingManager;
import com.handybook.handybook.configuration.event.ConfigurationEvent;
import com.handybook.handybook.configuration.manager.ConfigurationManager;
import com.handybook.handybook.core.constant.PrefsKey;
import com.handybook.handybook.core.data.DataManager;
import com.handybook.handybook.core.event.ActivityLifecycleEvent;
import com.handybook.handybook.core.event.HandyEvent;
import com.handybook.handybook.core.manager.AppBlockManager;
import com.handybook.handybook.core.manager.DefaultPreferencesManager;
import com.handybook.handybook.core.manager.SecurePreferencesManager;
import com.handybook.handybook.core.manager.ServicesManager;
import com.handybook.handybook.core.manager.StripeManager;
import com.handybook.handybook.core.manager.UserDataManager;
import com.handybook.handybook.deeplink.DeepLinkIntentProvider;
import com.handybook.handybook.helpcenter.helpcontact.manager.HelpContactManager;
import com.handybook.handybook.helpcenter.manager.HelpManager;
import com.handybook.handybook.library.util.DateTimeUtils;
import com.handybook.handybook.logger.handylogger.EventLogManager;
import com.handybook.handybook.notifications.manager.NotificationManager;
import com.handybook.handybook.promos.splash.SplashPromoManager;
import com.handybook.handybook.proteam.manager.ProTeamManager;
import com.handybook.handybook.push.manager.UrbanAirshipManager;
import com.handybook.handybook.referral.manager.ReferralsManager;
import com.handybook.shared.core.HandyLibrary;
import com.urbanairship.AirshipConfigOptions;
import com.urbanairship.UAirship;
import com.urbanairship.push.notifications.DefaultNotificationFactory;

import org.greenrobot.eventbus.EventBus;

import java.util.Date;
import java.util.Properties;

import javax.inject.Inject;

import dagger.ObjectGraph;
import io.fabric.sdk.android.Fabric;
import retrofit.RestAdapter;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

public class BaseApplication extends MultiDexApplication {

    private static final String TAG = BaseApplication.class.getName();

    public static final String FLAVOR_PROD = "prod";
    public static final String FLAVOR_STAGE = "stage";
    private static final long GA_SESSION_TIMEOUT_SECONDS = 600L;

    /**
     * If less than this threshold, that means the app was newly launched.
     */
    private static final long NEWLY_LAUNCH_THRESHOLD_IN_SECONDS = 5;
    private static GoogleAnalytics googleAnalytics;
    private static Tracker sTracker;
    //This is used for the application context

    private ObjectGraph mGraph;
    @Inject
    UserManager userManager;
    @Inject
    DataManager dataManager;
    @Inject
    EventBus bus;
    // We are injecting all of our event bus listening managers in BaseApplication to start them
    // up for event listening
    @Inject
    HelpManager helpManager;
    @Inject
    EventLogManager logEventsManager;
    @Inject
    HelpContactManager helpContactManager;
    @Inject
    SecurePreferencesManager mSecurePreferencesManager;
    @Inject
    DefaultPreferencesManager mDefaultPreferencesManager;
    @Inject
    AppBlockManager appBlockManager;
    @Inject
    StripeManager stripeManager;
    @Inject
    UserDataManager userDataManager;
    @Inject
    NotificationManager mNotificationManager;
    @Inject
    UrbanAirshipManager urbanAirshipManager;
    @Inject
    Properties properties;
    @Inject
    DeepLinkIntentProvider mDeepLinkIntentProvider;
    @Inject
    SplashPromoManager mSplashPromoManager;
    @Inject
    BookingEditManager bookingEditManager;
    @Inject
    ServicesManager servicesManager;
    @Inject
    ReferralsManager referralsManager;
    @Inject
    ConfigurationManager configurationManager;
    @Inject
    ProTeamManager proTeamManager;
    @Inject
    BookingManager mBookingManager;
    @Inject
    RestAdapter mRestAdapter;

    private Date mApplicationStartTime;
    private int started;
    private boolean savedInstance;

    private static BaseApplication sInstance;

    public static GoogleAnalytics googleAnalytics() {
        return googleAnalytics;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        sInstance = this;
        mGraph = createObjectGraph();
        inject(this);
        HandyLibrary.init(mRestAdapter, this, BuildConfig.FLAVOR.equals(FLAVOR_PROD));
        mApplicationStartTime = new Date();

        FacebookSdk.sdkInitialize(getApplicationContext());

        googleAnalytics = GoogleAnalytics.getInstance(this);
        sTracker = googleAnalytics.newTracker(R.xml.global_tracker);
        sTracker.enableExceptionReporting(true);
        sTracker.enableAdvertisingIdCollection(true);
        sTracker.setSessionTimeout(GA_SESSION_TIMEOUT_SECONDS);
        //tracker.enableAutoActivityTracking(true); // Using custom activity tracking for now
        final User user = userManager.getCurrentUser();
        if (user != null) {
            sTracker.setClientId(user.getId());
        }
        initFabric();
        initButton();
        final AirshipConfigOptions options = setupUrbanAirshipConfig();
        UAirship.takeOff(this, options, new UAirship.OnReadyCallback() {
            @Override
            public void onAirshipReady(final UAirship airship) {
                final DefaultNotificationFactory defaultNotificationFactory =
                        new DefaultNotificationFactory(getApplicationContext());
                defaultNotificationFactory.setColor(
                        ContextCompat.getColor(getApplicationContext(), R.color.handy_blue));
                defaultNotificationFactory.setSmallIconId(R.drawable.ic_notification);
                airship.getPushManager().setNotificationFactory(defaultNotificationFactory);
                airship.getPushManager().setPushEnabled(false);
                airship.getPushManager().setUserNotificationsEnabled(false);
            }
        });

        CalligraphyConfig.initDefault(
                new CalligraphyConfig.Builder()
                        .setDefaultFontPath("fonts/CircularStd-Book.otf")
                        .build()
        );

        if (mSecurePreferencesManager.getLong(PrefsKey.APP_FIRST_RUN, 0) == 0) {
            mSecurePreferencesManager.setLong(PrefsKey.APP_FIRST_RUN, System.currentTimeMillis());
        }

        registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(
                    final Activity activity,
                    final Bundle savedInstanceState
            ) {
                trackScreen(activity);
                bus.post(new ActivityLifecycleEvent.Created(activity, savedInstanceState));
                savedInstance = savedInstanceState != null;
            }

            @Override
            public void onActivityStarted(final Activity activity) {
                bus.post(new ActivityLifecycleEvent.Started(activity));
                ++started;
                if (started == 1) {
                    updateUser();
                }
            }

            @Override
            public void onActivityResumed(final Activity activity) {
                bus.post(new ActivityLifecycleEvent.Resumed(activity));
            }

            @Override
            public void onActivityPaused(final Activity activity) {
                bus.post(new ActivityLifecycleEvent.Paused(activity));
            }

            @Override
            public void onActivityStopped(final Activity activity) {
                bus.post(new ActivityLifecycleEvent.Stopped(activity));
            }

            @Override
            public void onActivitySaveInstanceState(
                    final Activity activity,
                    final Bundle outState
            ) {
                bus.post(new ActivityLifecycleEvent.SavedInstanceState(activity, outState));
            }

            @Override
            public void onActivityDestroyed(final Activity activity) {
                bus.post(new ActivityLifecycleEvent.Destroyed(activity));
            }
        });

        //warms up the configuration cache
        bus.post(new ConfigurationEvent.RequestConfiguration());

    }

    private void initFabric() {
        Fabric.with(this, new Crashlytics());
        Crashlytics.setUserIdentifier(
                Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID)
        );
        User currentUser = userManager.getCurrentUser();
        if (currentUser != null) {
            Crashlytics.setUserEmail(currentUser.getEmail());
        }
    }

    private void initButton() {
        if (BuildConfig.DEBUG) {
            com.usebutton.sdk.Button.enableDebugLogging();
        }
        com.usebutton.sdk.Button.getButton(this).start();
    }

    public void updateUser() {
        final User user = userManager.getCurrentUser();
        if (user != null) {
            bus.post(new HandyEvent.RequestUser(user.getId(), user.getAuthToken(), null));
        }
    }

    protected AirshipConfigOptions setupUrbanAirshipConfig() {
        AirshipConfigOptions options = AirshipConfigOptions.loadDefaultOptions(this);
        options.inProduction = BuildConfig.FLAVOR.equals(BaseApplication.FLAVOR_PROD);
        return options;
    }

    protected ObjectGraph createObjectGraph() {
        return ObjectGraph.create(new ApplicationModule(this));
    }

    public final void inject(final Object object) {
        mGraph.inject(object);
    }

    public final static BookingManager getBookingManager() {
        return sInstance.mBookingManager;
    }

    private static void track(final Activity activity, final String action) {
        BaseApplication.tracker().send(
                new HitBuilders.EventBuilder("activity_lifecycle", action)
                        .setLabel(activity.getLocalClassName())
                        .build()
        );
    }

    private static void trackScreen(final Activity activity) {
        sTracker.setScreenName(ScreenName.from(activity));
        sTracker.send(new HitBuilders.ScreenViewBuilder().build());
    }

    /**
     * Should be careful not to store the application context in a local variable, otherwise memory won't release
     * @return
     */
    public static Tracker tracker() {
        return sTracker;
    }

    /**
     * This application is considered newly launch if it's less than 5 seconds old.
     *
     * @return
     */
    public boolean isNewlyLaunched() {
        long diff = DateTimeUtils.getDiffInSeconds(new Date(), mApplicationStartTime);
        return diff <= NEWLY_LAUNCH_THRESHOLD_IN_SECONDS;
    }
}
