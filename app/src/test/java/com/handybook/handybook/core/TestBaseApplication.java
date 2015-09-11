package com.handybook.handybook.core;

import com.urbanairship.AirshipConfigOptions;

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
    protected AirshipConfigOptions setupUrbanAirshipConfig() {
        AirshipConfigOptions options = AirshipConfigOptions.loadDefaultOptions(this);
        options.inProduction = true;
        return options;
    }

    @Override
    protected void updateUser() {
    }
}
