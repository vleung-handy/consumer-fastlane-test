package com.handybook.handybook.core;

import android.support.annotation.NonNull;

import com.handybook.handybook.configuration.event.ConfigurationEvent;
import com.handybook.handybook.configuration.manager.ConfigurationManager;
import com.handybook.handybook.configuration.model.Configuration;
import com.handybook.handybook.core.data.DataManager;
import com.handybook.handybook.core.manager.DefaultPreferencesManager;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

public class CoreTestConfigManager extends ConfigurationManager {

    public CoreTestConfigManager(
            final Bus bus,
            final DefaultPreferencesManager defaultPreferencesManager,
            final DataManager dataManager
    ) {
        super(bus, defaultPreferencesManager, dataManager);
    }

    @NonNull
    @Override
    public Configuration getPersistentConfiguration() {
        Configuration config = super.getPersistentConfiguration();

        // Override the config here
        config.setSaveZipCodeEnabled(false);
        return config;
    }

    @Subscribe
    @Override
    public void onRequestConfiguration(final ConfigurationEvent.RequestConfiguration event) {
        super.onRequestConfiguration(event);
    }
}
