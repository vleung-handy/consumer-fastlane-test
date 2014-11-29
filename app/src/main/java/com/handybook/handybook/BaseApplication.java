package com.handybook.handybook;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

import com.newrelic.agent.android.NewRelic;

import javax.inject.Inject;

import dagger.ObjectGraph;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

public final class BaseApplication extends Application {
    static final String FLAVOR_PROD = "prod";
    static final String FLAVOR_STAGE = "stage";

    private ObjectGraph graph;
    private int started;
    private Activity lastActivity;
    private boolean savedInstance;

    @Inject UserManager userManager;
    @Inject DataManager dataManager;
    @Inject Mixpanel mixpanel;

    @Override
    public final void onCreate() {
        super.onCreate();
        graph = ObjectGraph.create(new ApplicationModule(this));
        inject(this);

        CalligraphyConfig.initDefault("fonts/CircularStd-Book.otf", R.attr.fontPath);

        if (BuildConfig.FLAVOR.equals(BaseApplication.FLAVOR_PROD)) {
            NewRelic.withApplicationToken("AA7a37dccf925fd1e474142399691d1b6b3f84648b").start(this);
        }
        else {
            NewRelic.withApplicationToken("AAbaf8c55fb9788d1664e82661d94bc18ea7c39aa6").start(this);
        }

        registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(final Activity activity,
                                          final Bundle savedInstanceState) {
                savedInstance = savedInstanceState != null;
            }

            @Override
            public void onActivityStarted(final Activity activity) {
                ++started;

                if (started == 1) {
                    if (!savedInstance) mixpanel.trackEventAppOpened(true);
                    else mixpanel.trackEventAppOpened(false);
                    updateUser();
                }

                if (lastActivity != null && lastActivity.getClass() == activity.getClass()) {
                    mixpanel.trackEventAppOpened(false);
                    updateUser();
                }
            }

            @Override
            public void onActivityResumed(final Activity activity) {}

            @Override
            public void onActivityPaused(final Activity activity) {
                lastActivity = activity;
            }

            @Override
            public void onActivityStopped(final Activity activity) {}

            @Override
            public void onActivitySaveInstanceState(final Activity activity,
                                                    final Bundle outState) {}

            @Override
            public void onActivityDestroyed(final Activity activity) {}
        });
    }

    final void inject(final Object object) {
        graph.inject(object);
    }

    private void updateUser() {
        final User user = userManager.getCurrentUser();

        if (user != null) {
            dataManager.getUser(user.getId(), user.getAuthToken(), new DataManager.Callback<User>() {
                @Override
                public void onSuccess(final User updatedUser) {
                    userManager.setCurrentUser(updatedUser);
                }

                @Override
                public void onError(final DataManager.DataManagerError error) {}
            });
        }
    }
}
