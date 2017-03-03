package com.handybook.handybook.core;

import dagger.ObjectGraph;

public class CoreTestApplication extends BaseApplication {

    @Override
    protected ObjectGraph createObjectGraph() {
        return ObjectGraph.create(new CoreTestApplicationModule(this));
    }
}
