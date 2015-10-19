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
        public static final String P = "p";
        public static final String S = "s";
    }

    private final Bus bus;
    private final PrefsManager prefsManager;

    public EnvironmentModifier(Context context, Bus bus, PrefsManager prefsManager)
    {
        this.bus = bus;
        this.prefsManager = prefsManager;

        try
        {
            Properties properties = PropertiesReader.getProperties(context, "override.properties");
            String environment = properties.getProperty("environment", Environment.S);
            environment = prefsManager.getString(PrefsKey.ENVIRONMENT_PREFIX, environment); // whatever is stored in prefs is higher priority

            prefsManager.setString(PrefsKey.ENVIRONMENT_PREFIX, environment);
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public String getEnvironment()
    {
        return prefsManager.getString(PrefsKey.ENVIRONMENT_PREFIX, Environment.S);
    }

    public boolean isProduction()
    {
        return Environment.P.equals(getEnvironment());
    }

    public void setEnvironment(String environment)
    {
        String previousEnvironment = getEnvironment();
        prefsManager.setString(PrefsKey.ENVIRONMENT_PREFIX, environment);

        bus.post(new EnvironmentUpdatedEvent(environment, previousEnvironment));
    }
}
