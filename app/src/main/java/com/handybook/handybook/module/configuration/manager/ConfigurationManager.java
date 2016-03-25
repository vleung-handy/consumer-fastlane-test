package com.handybook.handybook.module.configuration.manager;

import android.support.annotation.Nullable;

import com.crashlytics.android.Crashlytics;
import com.handybook.handybook.constant.PrefsKey;
import com.handybook.handybook.data.DataManager;
import com.handybook.handybook.manager.PrefsManager;
import com.handybook.handybook.module.configuration.event.ConfigurationEvent;
import com.handybook.handybook.module.configuration.model.Configuration;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import javax.inject.Inject;

public class ConfigurationManager
{
    private final Bus mBus;
    private PrefsManager mPrefsManager;
    private final DataManager mDataManager;

    @Inject
    public ConfigurationManager(
            final Bus bus,
            final PrefsManager prefsManager,
            final DataManager dataManager
    )
    {
        mBus = bus;
        mPrefsManager = prefsManager;
        mDataManager = dataManager;
        mBus.register(this);
    }

    @Subscribe
    public void onRequestConfiguration(final ConfigurationEvent.RequestConfiguration event)
    {
        final Configuration cachedConfiguration = getCachedConfiguration();
        if (cachedConfiguration != null)
        {
            mBus.post(new ConfigurationEvent.ReceiveConfigurationSuccess(cachedConfiguration));
        }
        else
        {
            mDataManager.requestConfiguration(new DataManager.Callback<Configuration>()
            {
                @Override
                public void onSuccess(final Configuration configuration)
                {
                    setCachedConfiguration(configuration);
                    mBus.post(new ConfigurationEvent.ReceiveConfigurationSuccess(configuration));
                }

                @Override
                public void onError(final DataManager.DataManagerError error)
                {
                    mBus.post(new ConfigurationEvent.ReceiveConfigurationError(error));
                }
            });
        }
    }

    @Subscribe
    public void onRefreshConfiguration(final ConfigurationEvent.RefreshConfiguration event)
    {
        mDataManager.requestConfiguration(new DataManager.Callback<Configuration>()
        {
            @Override
            public void onSuccess(final Configuration configuration)
            {
                setCachedConfiguration(configuration);
            }

            @Override
            public void onError(final DataManager.DataManagerError error)
            {
            }
        });
    }

    private void setCachedConfiguration(final Configuration configuration)
    {
        mPrefsManager.setString(PrefsKey.CONFIGURATION, configuration.toJson());
    }

    // Do NOT make this public. I know what you're trying to do.
    @Nullable
    private Configuration getCachedConfiguration()
    {
        final String configurationJson = mPrefsManager.getString(PrefsKey.CONFIGURATION);
        Configuration config = null;

        if (configurationJson != null)
        {
            try
            {
                config = Configuration.fromJson(configurationJson);
            }
            catch (Exception e)
            {
                //if there is ever an error parsing this, fall out and let it create a new set
                Crashlytics.logException(new RuntimeException("JSON: " + configurationJson, e));
            }

        }

        return config;
    }
}
