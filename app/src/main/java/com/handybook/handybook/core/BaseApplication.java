package com.handybook.handybook.core;

import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.multidex.MultiDexApplication;

import com.crashlytics.android.Crashlytics;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.handybook.handybook.BuildConfig;
import com.handybook.handybook.R;
import com.handybook.handybook.booking.bookingedit.manager.BookingEditManager;
import com.handybook.handybook.constant.PrefsKey;
import com.handybook.handybook.data.DataManager;
import com.handybook.handybook.deeplink.DeepLinkIntentProvider;
import com.handybook.handybook.event.ActivityLifecycleEvent;
import com.handybook.handybook.event.HandyEvent;
import com.handybook.handybook.helpcenter.helpcontact.manager.HelpContactManager;
import com.handybook.handybook.helpcenter.manager.HelpManager;
import com.handybook.handybook.logger.handylogger.EventLogManager;
import com.handybook.handybook.logger.mixpanel.Mixpanel;
import com.handybook.handybook.manager.AppBlockManager;
import com.handybook.handybook.manager.PrefsManager;
import com.handybook.handybook.manager.ServicesManager;
import com.handybook.handybook.manager.StripeManager;
import com.handybook.handybook.manager.UserDataManager;
import com.handybook.handybook.module.configuration.manager.ConfigurationManager;
import com.handybook.handybook.module.notifications.feed.manager.NotificationManager;
import com.handybook.handybook.module.notifications.splash.manager.SplashNotificationManager;
import com.handybook.handybook.module.proteam.manager.ProTeamManager;
import com.handybook.handybook.module.push.manager.UrbanAirshipManager;
import com.handybook.handybook.module.referral.manager.ReferralsManager;
import com.handybook.handybook.util.DateTimeUtils;
import com.newrelic.agent.android.NewRelic;
import com.squareup.otto.Bus;
import com.urbanairship.AirshipConfigOptions;
import com.urbanairship.UAirship;
import com.urbanairship.push.notifications.DefaultNotificationFactory;

import java.util.Date;
import java.util.Properties;

import javax.inject.Inject;

import dagger.ObjectGraph;
import io.fabric.sdk.android.Fabric;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

public class BaseApplication extends MultiDexApplication
{
    public static final String FLAVOR_PROD = "prod";
    public static final String FLAVOR_STAGE = "stage";
    private static final long GA_SESSION_TIMEOUT_SECONDS = 600L;

    /**
     * If less than this threshold, that means the app was newly launched.
     */
    private static final long NEWLY_LAUNCH_THRESHOLD_IN_SECONDS = 5;
    private static GoogleAnalytics googleAnalytics;
    private static Tracker tracker;
    private static String sDeviceId = "";

    protected ObjectGraph graph;
    @Inject
    UserManager userManager;
    @Inject
    DataManager dataManager;
    @Inject
    Mixpanel mixpanel;
    @Inject
    Bus bus;
    // We are injecting all of our event bus listening managers in BaseApplication to start them
    // up for event listening
    @Inject
    HelpManager helpManager;
    @Inject
    EventLogManager logEventsManager;
    @Inject
    HelpContactManager helpContactManager;
    @Inject
    PrefsManager prefsManager;
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
    SplashNotificationManager mSplashNotificationManager;
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

    private Date mApplicationStartTime;
    private int started;
    private boolean savedInstance;

    public static GoogleAnalytics googleAnalytics()
    {
        return googleAnalytics;
    }

    @Override
    public void onCreate()
    {
        super.onCreate();
        mApplicationStartTime = new Date();

        googleAnalytics = GoogleAnalytics.getInstance(this);
        tracker = googleAnalytics.newTracker(R.xml.global_tracker);
        tracker.enableExceptionReporting(true);
        tracker.enableAdvertisingIdCollection(true);
        tracker.setSessionTimeout(GA_SESSION_TIMEOUT_SECONDS);
        sDeviceId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);

        //tracker.enableAutoActivityTracking(true); // Using custom activity tracking for now
        createObjectGraph();
        final User user = userManager.getCurrentUser();
        if (user != null)
        {
            tracker.setClientId(user.getId());
        }
        initFabric();
        initButton();
        final AirshipConfigOptions options = setupUrbanAirshipConfig();
        UAirship.takeOff(this, options, new UAirship.OnReadyCallback()
        {
            @Override
            public void onAirshipReady(final UAirship airship)
            {
                final DefaultNotificationFactory defaultNotificationFactory =
                        new DefaultNotificationFactory(getApplicationContext());
                defaultNotificationFactory.setColor(getResources().getColor(R.color.handy_blue));
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

        if (BuildConfig.FLAVOR.equals(BaseApplication.FLAVOR_PROD))
        {
            NewRelic.withApplicationToken(properties.getProperty("new_relic_key")).start(this);
        }
        else
        {
            NewRelic.withApplicationToken(properties.getProperty("new_relic_key_internal")).start(this);
        }
        // If this is the first ever run of the application, emit Mixpanel event
        if (prefsManager.getLong(PrefsKey.APP_FIRST_RUN, 0) == 0)
        {
            prefsManager.setLong(PrefsKey.APP_FIRST_RUN, System.currentTimeMillis());
        }
        registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks()
        {
            @Override
            public void onActivityCreated(
                    final Activity activity,
                    final Bundle savedInstanceState
            )
            {
                trackScreen(activity);
                bus.post(new ActivityLifecycleEvent.Created(activity, savedInstanceState));
                savedInstance = savedInstanceState != null;
            }

            @Override
            public void onActivityStarted(final Activity activity)
            {
                bus.post(new ActivityLifecycleEvent.Started(activity));
                ++started;
                if (started == 1)
                {
                    if (!savedInstance)
                    {
                        mixpanel.trackEventAppOpened(true);
                    }
                    else
                    {
                        mixpanel.trackEventAppOpened(false);
                    }
                    updateUser();
                }
            }

            @Override
            public void onActivityResumed(final Activity activity)
            {
                bus.post(new ActivityLifecycleEvent.Resumed(activity));
            }

            @Override
            public void onActivityPaused(final Activity activity)
            {
                bus.post(new ActivityLifecycleEvent.Paused(activity));
            }

            @Override
            public void onActivityStopped(final Activity activity)
            {
                bus.post(new ActivityLifecycleEvent.Stopped(activity));
            }

            @Override
            public void onActivitySaveInstanceState(
                    final Activity activity,
                    final Bundle outState
            )
            {
                bus.post(new ActivityLifecycleEvent.SavedInstanceState(activity, outState));
            }

            @Override
            public void onActivityDestroyed(final Activity activity)
            {
                bus.post(new ActivityLifecycleEvent.Destroyed(activity));
            }
        });
    }

    private void initFabric()
    {
        Fabric.with(this, new Crashlytics());
        Crashlytics.setUserIdentifier(
                Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID)
        );
        User currentUser = userManager.getCurrentUser();
        if (currentUser != null)
        {
            Crashlytics.setUserEmail(currentUser.getEmail());
        }
    }

    private void initButton()
    {
        if (BuildConfig.DEBUG)
        {
            com.usebutton.sdk.Button.enableDebugLogging();
        }
        com.usebutton.sdk.Button.getButton(this).start();
    }

    public void updateUser()
    {
        final User user = userManager.getCurrentUser();
        if (user != null)
        {
            bus.post(new HandyEvent.RequestUser(user.getId(), user.getAuthToken(), null));
        }
    }

    protected AirshipConfigOptions setupUrbanAirshipConfig()
    {
        AirshipConfigOptions options = AirshipConfigOptions.loadDefaultOptions(this);
        options.inProduction = BuildConfig.FLAVOR.equals(BaseApplication.FLAVOR_PROD);
        return options;
    }

    protected void createObjectGraph()
    {
        graph = ObjectGraph.create(new ApplicationModule(this));
        inject(this);
    }

    public final void inject(final Object object)
    {
        graph.inject(object);
    }

    private static void track(final Activity activity, final String action)
    {
        BaseApplication.tracker().send(
                new HitBuilders.EventBuilder("activity_lifecycle", action)
                        .setLabel(activity.getLocalClassName())
                        .build()
        );
    }

    private static void trackScreen(final Activity activity)
    {
        tracker.setScreenName(ScreenName.from(activity));
        tracker.send(new HitBuilders.ScreenViewBuilder().build());
    }

    public static Tracker tracker()
    {
        return tracker;
    }

    public static String getDeviceId() { return sDeviceId; }

    public static String getDeviceModel()
    {
        final String manufacturer = Build.MANUFACTURER;
        final String model = Build.MODEL;

        if (model.startsWith(manufacturer))
        {
            return model;
        }
        else
        {
            return manufacturer + " " + model;
        }
    }

    /**
     * This application is considered newly launch if it's less than 5 seconds old.
     *
     * @return
     */
    public boolean isNewlyLaunched()
    {
        long diff = DateTimeUtils.getDiffInSeconds(new Date(), mApplicationStartTime);
        return diff <= NEWLY_LAUNCH_THRESHOLD_IN_SECONDS;
    }
}
