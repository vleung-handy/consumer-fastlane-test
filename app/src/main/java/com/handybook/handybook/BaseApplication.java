package com.handybook.handybook;

import android.app.Application;

import com.newrelic.agent.android.NewRelic;

import dagger.ObjectGraph;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

public final class BaseApplication extends Application {
    static final String FLAVOR_PROD = "prod";
    static final String FLAVOR_STAGE = "stage";

    private ObjectGraph graph;

    @Override
    public final void onCreate() {
        super.onCreate();
        graph = ObjectGraph.create(new ApplicationModule(this));

        CalligraphyConfig.initDefault("fonts/CircularStd-Book.otf", R.attr.fontPath);

        if (BuildConfig.FLAVOR.equals(BaseApplication.FLAVOR_PROD)) {
            NewRelic.withApplicationToken("AA7a37dccf925fd1e474142399691d1b6b3f84648b").start(this);
        }
        else {
            NewRelic.withApplicationToken("AAbaf8c55fb9788d1664e82661d94bc18ea7c39aa6").start(this);
        }
    }

    final void inject(final Object object) {
        graph.inject(object);
    }
}
