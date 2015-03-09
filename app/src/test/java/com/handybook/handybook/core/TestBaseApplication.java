package com.handybook.handybook.core;

import android.app.Application;

import com.handybook.handybook.BuildConfig;
import com.handybook.handybook.R;
import com.urbanairship.AirshipConfigOptions;
import com.urbanairship.UAirship;
import com.urbanairship.push.notifications.DefaultNotificationFactory;

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
