package com.handybook.handybook.core;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import android.support.multidex.MultiDexApplication;

import com.crashlytics.android.Crashlytics;
import com.handybook.handybook.BuildConfig;
import com.handybook.handybook.R;
import com.handybook.handybook.constant.PrefsKey;
import com.handybook.handybook.data.DataManager;
import com.handybook.handybook.data.Mixpanel;
import com.handybook.handybook.event.ActivityEvent;
import com.handybook.handybook.manager.AppBlockManager;
import com.handybook.handybook.manager.HelpContactManager;
import com.handybook.handybook.manager.HelpManager;
import com.handybook.handybook.manager.PrefsManager;
import com.newrelic.agent.android.NewRelic;
import com.squareup.otto.Bus;
import com.urbanairship.AirshipConfigOptions;
import com.urbanairship.UAirship;
import com.urbanairship.push.notifications.DefaultNotificationFactory;

import javax.inject.Inject;

import dagger.ObjectGraph;
import io.fabric.sdk.android.Fabric;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

public class BaseApplication extends MultiDexApplication
{
    public static final String FLAVOR_PROD = "prod";
    public static final String FLAVOR_STAGE = "stage";

    protected ObjectGraph graph;
    private int started;
    private boolean savedInstance;

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
    HelpContactManager helpContactManager;
    @Inject
    PrefsManager prefsManager;
    @Inject
    AppBlockManager appBlockManager;


    @Override
    public void onCreate()
    {
        super.onCreate();
        Fabric.with(this, new Crashlytics());

        createObjectGraph();

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
            NewRelic.withApplicationToken("AA7a37dccf925fd1e474142399691d1b6b3f84648b").start(this);
        } else
        {
            NewRelic.withApplicationToken("AAbaf8c55fb9788d1664e82661d94bc18ea7c39aa6").start(this);
        }
        // If this is the first ever run of the application, emit Mixpanel event
        if (prefsManager.getLong(PrefsKey.APP_FIRST_RUN, 0) == 0)
        {
            prefsManager.setLong(PrefsKey.APP_FIRST_RUN, System.currentTimeMillis());
            mixpanel.trackEventAppTrackInstall();
        }
        registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks()
        {
            @Override
            public void onActivityCreated(final Activity activity,
                    final Bundle savedInstanceState)
            {
                bus.post(new ActivityEvent.Created(activity, savedInstanceState));
                savedInstance = savedInstanceState != null;
            }

            @Override
            public void onActivityStarted(final Activity activity)
            {
                bus.post(new ActivityEvent.Started(activity));
                ++started;
                if (started == 1)
                {
                    if (!savedInstance)
                    {
                        mixpanel.trackEventAppOpened(true);
                    } else
                    {
                        mixpanel.trackEventAppOpened(false);
                    }
                    updateUser();
                }
            }

            @Override
            public void onActivityResumed(final Activity activity)
            {
                bus.post(new ActivityEvent.Resumed(activity));
            }

            @Override
            public void onActivityPaused(final Activity activity)
            {
                bus.post(new ActivityEvent.Paused(activity));
            }

            @Override
            public void onActivityStopped(final Activity activity)
            {
                bus.post(new ActivityEvent.Stopped(activity));
            }

            @Override
            public void onActivitySaveInstanceState(
                    final Activity activity,
                    final Bundle outState)
            {
                bus.post(new ActivityEvent.SavedInstanceState(activity, outState));
            }

            @Override
            public void onActivityDestroyed(final Activity activity)
            {
                bus.post(new ActivityEvent.Destroyed(activity));
            }
        });
    }

    public final void inject(final Object object)
    {
        graph.inject(object);
    }

    protected void updateUser()
    {
        final User user = userManager.getCurrentUser();
        if (user != null)
        {
            dataManager.getUser(user.getId(), user.getAuthToken(), new DataManager.Callback<User>()
            {
                @Override
                public void onSuccess(final User updatedUser)
                {
                    userManager.setCurrentUser(updatedUser);
                }

                @Override
                public void onError(final DataManager.DataManagerError error)
                {
                }
            });
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
}
