package com.handybook.handybook.module.configuration.manager;

import android.support.annotation.Nullable;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.handybook.handybook.data.DataManager;
import com.handybook.handybook.manager.PrefsManager;
import com.handybook.handybook.module.configuration.event.ConfigurationEvent;
import com.handybook.handybook.module.configuration.model.Configuration;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

public class ConfigurationManager
{

    private final Bus mBus;
    private PrefsManager mPrefsManager;
    private final DataManager mDataManager;
    private static final String KEY_CONFIGURATION_CACHE = "configuration";

    private final String TAG = ConfigurationManager.class.getName();

    private Cache<String, Configuration> mConfigurationCache;

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

        mConfigurationCache = CacheBuilder.newBuilder()
                .maximumSize(1)
                .expireAfterWrite(3, TimeUnit.MINUTES)
                .build();
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
        mConfigurationCache.put(KEY_CONFIGURATION_CACHE, configuration);
    }

    // Do NOT make this public. I know what you're trying to do.
    @Nullable
    private Configuration getCachedConfiguration()
    {
        return mConfigurationCache.getIfPresent(KEY_CONFIGURATION_CACHE);
    }
}
