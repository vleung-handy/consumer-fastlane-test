package com.handybook.handybook.core;

import android.content.Context;

import com.handybook.handybook.BuildConfig;
import com.handybook.handybook.constant.PrefsKey;
import com.handybook.handybook.data.PropertiesReader;
import com.handybook.handybook.event.EnvironmentUpdatedEvent;
import com.handybook.handybook.manager.DefaultPreferencesManager;
import com.squareup.otto.Bus;

import java.util.Properties;

public class EnvironmentModifier
{
    public static final class Environment
    {
        public static final String PRODUCTION = "p";
        public static final String STAGING = "s";
        public static final String LOCAL = "l";
    }


    private final Bus mBus;
    private final DefaultPreferencesManager mDefaultPreferencesManager;

    public EnvironmentModifier(
            Context context,
            Bus bus,
            DefaultPreferencesManager defaultPreferencesManager
    )
    {
        mBus = bus;
        mDefaultPreferencesManager = defaultPreferencesManager;

        try
        {
            Properties properties = PropertiesReader.getProperties(context, "override.properties");
            String environment = properties.getProperty("environment", Environment.STAGING);
            environment = mDefaultPreferencesManager.getString(
                    PrefsKey.ENVIRONMENT_PREFIX,
                    environment
            ); // whatever is stored in prefs is higher priority

            mDefaultPreferencesManager.setString(PrefsKey.ENVIRONMENT_PREFIX, environment);
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public String getEnvironment()
    {
        String defaultEnvironment = BuildConfig.FLAVOR.equals(BaseApplication.FLAVOR_PROD) ?
                Environment.PRODUCTION : Environment.STAGING;

        String response = mDefaultPreferencesManager.getString(
                PrefsKey.ENVIRONMENT_PREFIX,
                defaultEnvironment
        );

        if (android.text.TextUtils.isEmpty(response))
        {
            return defaultEnvironment;
        }
        else
        {
            return response;
        }
    }

    public boolean isStaging()
    {
        return Environment.STAGING.equals(getEnvironment());
    }

    public boolean isProduction()
    {
        return Environment.PRODUCTION.equals(getEnvironment());
    }

    public boolean isLocal()
    {
        return Environment.LOCAL.equals(getEnvironment());
    }

    public void setEnvironment(String environment)
    {
        String previousEnvironment = getEnvironment();
        mDefaultPreferencesManager.setString(PrefsKey.ENVIRONMENT_PREFIX, environment);

        mBus.post(new EnvironmentUpdatedEvent(environment, previousEnvironment));
    }
}
