package com.handybook.handybook.core;

import dagger.ObjectGraph;

public class CoreTestApplication extends BaseApplication {

    @Override
    protected void createObjectGraph() {
        graph = ObjectGraph.create(new CoreTestApplicationModule(this));
        inject(this);
    }
}
