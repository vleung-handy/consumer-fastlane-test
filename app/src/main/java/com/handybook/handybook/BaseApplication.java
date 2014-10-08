package com.handybook.handybook;

import android.app.Application;

import dagger.ObjectGraph;

public final class BaseApplication extends Application {
    private final ObjectGraph graph = ObjectGraph.create(new ApplicationModule());
    final void inject(Object object) {
        graph.inject(object);
    }
}
