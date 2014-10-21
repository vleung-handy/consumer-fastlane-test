package com.handybook.handybook;

import android.app.Application;

import dagger.ObjectGraph;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

public final class BaseApplication extends Application {
    static final String FLAVOR_PROD = "prod";
    static final String FLAVOR_STAGE = "stage";

    private final ObjectGraph graph = ObjectGraph.create(new ApplicationModule(this));
    final void inject(final Object object) {
        graph.inject(object);
    }

    @Override
    public final void onCreate() {
        super.onCreate();
        CalligraphyConfig.initDefault("fonts/CircularStd-Book.otf", R.attr.fontPath);
    }
}
