package com.handybook.handybook;

import android.app.Application;

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
    }

    final void inject(final Object object) {
        graph.inject(object);
    }
}
