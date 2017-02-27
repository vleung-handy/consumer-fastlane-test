package com.handybook.handybook.core;

import android.support.annotation.Nullable;

import com.handybook.handybook.configuration.manager.ConfigurationManager;
import com.handybook.handybook.configuration.model.Configuration;
import com.handybook.handybook.core.data.DataManager;
import com.handybook.handybook.core.manager.DefaultPreferencesManager;
import com.squareup.otto.Bus;

public class TestConfigurationManager extends ConfigurationManager {

    public TestConfigurationManager(
            final Bus bus,
            final DefaultPreferencesManager defaultPreferencesManager,
            final DataManager dataManager
    ) {
        super(bus, defaultPreferencesManager, dataManager);
    }

    @Nullable
    @Override
    public Configuration getPersistentConfiguration() {
        return new Configuration();
    }
}
