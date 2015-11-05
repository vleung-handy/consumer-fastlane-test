package com.handybook.handybook.core;

import android.content.Context;

import com.handybook.handybook.constant.PrefsKey;
import com.handybook.handybook.data.PropertiesReader;
import com.handybook.handybook.event.EnvironmentUpdatedEvent;
import com.handybook.handybook.manager.PrefsManager;
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
    private final PrefsManager mPrefsManager;

    public EnvironmentModifier(Context context, Bus bus, PrefsManager prefsManager)
    {
        mBus = bus;
        mPrefsManager = prefsManager;

        try
        {
            Properties properties = PropertiesReader.getProperties(context, "override.properties");
            String environment = properties.getProperty("environment", Environment.STAGING);
            environment = prefsManager.getString(PrefsKey.ENVIRONMENT_PREFIX, environment); // whatever is stored in prefs is higher priority

            prefsManager.setString(PrefsKey.ENVIRONMENT_PREFIX, environment);
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public String getEnvironment()
    {
        return mPrefsManager.getString(PrefsKey.ENVIRONMENT_PREFIX, Environment.STAGING);
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
        mPrefsManager.setString(PrefsKey.ENVIRONMENT_PREFIX, environment);

        mBus.post(new EnvironmentUpdatedEvent(environment, previousEnvironment));
    }
}
