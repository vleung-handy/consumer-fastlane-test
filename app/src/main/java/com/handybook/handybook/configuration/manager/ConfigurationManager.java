package com.handybook.handybook.configuration.manager;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.handybook.handybook.configuration.event.ConfigurationEvent;
import com.handybook.handybook.configuration.model.Configuration;
import com.handybook.handybook.core.constant.PrefsKey;
import com.handybook.handybook.core.data.DataManager;
import com.handybook.handybook.core.manager.DefaultPreferencesManager;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

public class ConfigurationManager {

    private final Bus mBus;
    private final DataManager mDataManager;
    private final DefaultPreferencesManager mDefaultPreferencesManager;
    private static final String KEY_CONFIGURATION_CACHE = "configuration";

    private Cache<String, Configuration> mConfigurationCache;
    private Configuration mLastKnowConfiguration = null;

    @Inject
    public ConfigurationManager(
            final Bus bus,
            final DefaultPreferencesManager defaultPreferencesManager,
            final DataManager dataManager
    ) {
        mBus = bus;
        mDefaultPreferencesManager = defaultPreferencesManager;
        mDataManager = dataManager;
        mBus.register(this);

        mConfigurationCache = CacheBuilder.newBuilder()
                                          .maximumSize(1)
                                          .expireAfterWrite(3, TimeUnit.MINUTES)
                                          .build();
    }

    @Subscribe
    public void onRequestConfiguration(final ConfigurationEvent.RequestConfiguration event) {
        final Configuration cachedConfiguration = getCachedConfiguration();
        if (cachedConfiguration != null) {
            mBus.post(new ConfigurationEvent.ReceiveConfigurationSuccess(cachedConfiguration));
        }
        else {
            mDataManager.requestConfiguration(new DataManager.Callback<Configuration>() {
                @Override
                public void onSuccess(final Configuration configuration) {
                    setCachedConfiguration(configuration);
                    mBus.post(new ConfigurationEvent.ReceiveConfigurationSuccess(configuration));
                }

                @Override
                public void onError(final DataManager.DataManagerError error) {
                    mBus.post(new ConfigurationEvent.ReceiveConfigurationError(error));
                }
            });
        }
    }

    @Subscribe
    public void onRefreshConfiguration(final ConfigurationEvent.RefreshConfiguration event) {
        mDataManager.requestConfiguration(new DataManager.Callback<Configuration>() {
            @Override
            public void onSuccess(final Configuration configuration) {
                setCachedConfiguration(configuration);
            }

            @Override
            public void onError(final DataManager.DataManagerError error) {
            }
        });
    }

    public void invalidateCache() {
        mConfigurationCache.invalidate(KEY_CONFIGURATION_CACHE);
    }

    private void setCachedConfiguration(final Configuration configuration) {
        mConfigurationCache.put(KEY_CONFIGURATION_CACHE, configuration);
        mLastKnowConfiguration = configuration;
        //also put this in the prefs manager, because sometimes we might need access to this before
        //the onResume state.
        mDefaultPreferencesManager.setString(PrefsKey.CONFIGURATION, configuration.toJson());
    }

    @Nullable
    public Configuration getCachedConfiguration() {
        return mConfigurationCache.getIfPresent(KEY_CONFIGURATION_CACHE);
    }

    /**
     * This returns the last known configuration, which could be very stale.
     *
     * @return
     */
    public Configuration getLastKnowConfiguration() {
        return mLastKnowConfiguration;
    }

    /**
     * Returns a cached configuration if we have one, otherwise we try looking on disk
     *
     * @return
     */
    @NonNull
    public Configuration getPersistentConfiguration() {
        Configuration rval = getCachedConfiguration();

        if (rval == null) {
            String json = mDefaultPreferencesManager.getString(PrefsKey.CONFIGURATION, null);

            if (!TextUtils.isEmpty(json)) {
                rval = Configuration.fromJson(json);
            }
        }

        if (rval == null) {
            rval = new Configuration();
        }
        return rval;
    }
}
