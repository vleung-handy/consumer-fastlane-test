package com.handybook.handybook.core;

import android.app.Activity;
import android.app.Application;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;

import com.crashlytics.android.Crashlytics;
import com.handybook.handybook.BuildConfig;
import com.handybook.handybook.R;
import com.handybook.handybook.constant.PrefsKey;
import com.handybook.handybook.data.DataManager;
import com.handybook.handybook.data.Mixpanel;
import com.handybook.handybook.manager.HelpContactManager;
import com.handybook.handybook.manager.HelpManager;
import com.handybook.handybook.manager.PrefsManager;
import com.handybook.handybook.ui.activity.BlockingActivity;
import com.newrelic.agent.android.NewRelic;
import com.urbanairship.AirshipConfigOptions;
import com.urbanairship.UAirship;
import com.urbanairship.push.notifications.DefaultNotificationFactory;

import javax.inject.Inject;

import dagger.ObjectGraph;
import io.fabric.sdk.android.Fabric;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

public class BaseApplication extends Application
{
    public static final String FLAVOR_PROD = "prod";
    public static final String FLAVOR_STAGE = "stage";
    public static final long MIN_APP_BLOCKED_CHECK_INTERVAL = 30 * 1000; // No more than every 30s

    protected ObjectGraph graph;
    private int started;
    private boolean savedInstance;

    @Inject
    UserManager userManager;
    @Inject
    DataManager dataManager;
    @Inject
    Mixpanel mixpanel;


    //We are injecting all of our event bus listening managers in BaseApplication to start them up for event listening
    @Inject
    HelpManager helpManager;
    @Inject
    HelpContactManager helpContactManager;
    @Inject
    PrefsManager prefsManager;


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

        registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks()
        {
            @Override
            public void onActivityCreated(final Activity activity,
                    final Bundle savedInstanceState)
            {
                savedInstance = savedInstanceState != null;
            }

            @Override
            public void onActivityStarted(final Activity activity)
            {
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
                if (shoulBlockActivity(activity))
                {
                    activity.finish();
                    showBlockingScreen();
                }
            }

            @Override
            public void onActivityPaused(final Activity activity)
            {
            }

            @Override
            public void onActivityStopped(final Activity activity)
            {
            }

            @Override
            public void onActivitySaveInstanceState(final Activity activity,
                    final Bundle outState)
            {
            }

            @Override
            public void onActivityDestroyed(final Activity activity)
            {
            }
        });
    }

    private boolean shoulBlockActivity(final Activity activity)
    {
        // If we are resuming an activity other than blocking activity and app is blocked
        return isAppBlocked() && !activity.getClass().equals(BlockingActivity.class);
    }

    private void showBlockingScreen()
    {
        Intent launchBlockingActivity = new Intent(this, BlockingActivity.class);
        launchBlockingActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        launchBlockingActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(launchBlockingActivity);
    }

    private boolean isAppBlocked()
    {
        if (shouldUpdateBlockingStateFromApi())
        {
            updateBlockingStateFromApi();
        }
        return prefsManager.getBoolean(PrefsKey.APP_BLOCKED, false);
    }

    private boolean shouldUpdateBlockingStateFromApi()
    {
        final long lastBlockedCheckMillis = prefsManager.getLong(PrefsKey.APP_BLOCKED_LAST_CHECK, 0);
        return System.currentTimeMillis() - lastBlockedCheckMillis > MIN_APP_BLOCKED_CHECK_INTERVAL;
    }

    /**
     * Request ShouldBlock object from API and update shared preferences accordingly
     */
    private void updateBlockingStateFromApi()
    {
        int versionCode;
        try
        {
            versionCode = getPackageManager().getPackageInfo(getPackageName(), 0).versionCode;
        } catch (PackageManager.NameNotFoundException nnfe)
        {
            nnfe.printStackTrace();
            Crashlytics.logException(nnfe);
            versionCode = 0;
        }
        dataManager.getShouldBlockObject(
                versionCode,
                new DataManager.CacheResponse<ShouldBlockObject>()
                {
                    @Override
                    public void onResponse(final ShouldBlockObject shouldBlockObject)
                    {
                        //Do nothing, what is this even for?
                    }
                },
                new DataManager.Callback<ShouldBlockObject>()
                {
                    @Override
                    public void onSuccess(ShouldBlockObject response)
                    {
                        updateAppBlockedSharedPreference(response.isBlocked());
                    }

                    @Override
                    public void onError(DataManager.DataManagerError error)
                    {
                        Crashlytics.log("Get should block object error.");
                        // Otherwise do nothing. We default to letting people use the app, so in
                        // case of request error we stick to that policy.
                    }
                });

    }

    private void updateAppBlockedSharedPreference(final boolean isBlocked)
    {
        prefsManager.setLong(PrefsKey.APP_BLOCKED_LAST_CHECK, System.currentTimeMillis());
        prefsManager.setBoolean(PrefsKey.APP_BLOCKED, isBlocked);
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
