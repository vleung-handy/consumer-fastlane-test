package com.handybook.handybook.module.configuration.manager;

import android.content.SharedPreferences;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.handybook.handybook.constant.PrefsKey;
import com.handybook.handybook.data.DataManager;
import com.handybook.handybook.module.configuration.event.ConfigurationEvent;
import com.handybook.handybook.module.configuration.model.Configuration;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

public class ConfigurationManager
{

    private final Bus mBus;
    private SharedPreferences mSharedPreferences;
    private final DataManager mDataManager;
    private static final String KEY_CONFIGURATION_CACHE = "configuration";

    private Cache<String, Configuration> mConfigurationCache;

    @Inject
    public ConfigurationManager(
            final Bus bus,
            final SharedPreferences sharedPreferences,
            final DataManager dataManager
    )
    {
        mBus = bus;
        mSharedPreferences = sharedPreferences;
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
                    mBus.post(new ConfigurationEvent.ReceiveConfigurationSuccess(configuration));
                    setCachedConfiguration(configuration);
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

    public void invalidateCache()
    {
        mConfigurationCache.invalidate(KEY_CONFIGURATION_CACHE);
    }

    private void setCachedConfiguration(final Configuration configuration)
    {
        mConfigurationCache.put(KEY_CONFIGURATION_CACHE, configuration);

        //also put this in the prefs manager, because sometimes we might need access to this before
        //the onResume state.
        final SharedPreferences.Editor edit = mSharedPreferences.edit();
        edit.putString(PrefsKey.CONFIGURATION.getKey(), configuration.toJson());
        edit.apply();
    }

    // Do NOT make this public. I know what you're trying to do.
    @Nullable
    private Configuration getCachedConfiguration()
    {
        return mConfigurationCache.getIfPresent(KEY_CONFIGURATION_CACHE);
    }

    /**
     * Returns a cached configuration if we have one, otherwise we try looking on disk
     *
     * @return
     */
    @Nullable
    public Configuration getPersistentConfiguration()
    {
        Configuration rval = getCachedConfiguration();

        if (rval == null)
        {
            String json = mSharedPreferences.getString(PrefsKey.CONFIGURATION.getKey(), null);

            if (!TextUtils.isEmpty(json))
            {
                rval = Configuration.fromJson(json);
            }
        }

        return rval;
    }
}
