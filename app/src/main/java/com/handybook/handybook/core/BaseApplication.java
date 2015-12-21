package com.handybook.handybook.core;

import android.app.Activity;
import android.os.Bundle;
import android.support.multidex.MultiDexApplication;

import com.crashlytics.android.Crashlytics;
import com.handybook.handybook.BuildConfig;
import com.handybook.handybook.constant.PrefsKey;
import com.handybook.handybook.data.DataManager;
import com.handybook.handybook.data.Mixpanel;
import com.handybook.handybook.event.ActivityEvent;
import com.handybook.handybook.helpcenter.helpcontact.manager.HelpContactManager;
import com.handybook.handybook.helpcenter.manager.HelpManager;
import com.handybook.handybook.manager.AppBlockManager;
import com.handybook.handybook.manager.PrefsManager;
import com.handybook.handybook.manager.StripeManager;
import com.handybook.handybook.module.push.manager.UrbanAirshipManager;
import com.handybook.handybook.manager.UserDataManager;
import com.newrelic.agent.android.NewRelic;
import com.squareup.otto.Bus;

import java.util.Properties;

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
    @Inject
    StripeManager stripeManager;
    @Inject
    UserDataManager userDataManager;
    @Inject
    UrbanAirshipManager urbanAirshipManager;
    @Inject
    Properties properties;

    @Override
    public void onCreate()
    {
        super.onCreate();
        createObjectGraph();
        Fabric.with(this, new Crashlytics());

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

    public void updateUser()
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

    protected void createObjectGraph()
    {
        graph = ObjectGraph.create(new ApplicationModule(this));
        inject(this);
    }
}
