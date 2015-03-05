package com.handybook.handybook.core;

import android.app.Application;
import dagger.ObjectGraph;

/**
 * Created by jwilliams on 3/3/15.
 */
public class TestBaseApplication extends BaseApplication {

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    protected void createObjectGraph() {
        graph = ObjectGraph.create(new TestModule(this));
        graph.inject(this);
    }

    @Override
    protected void setupUrbanAirship() {
    }
}
