package com.handybook.handybook.core;

import android.content.Context;

import com.handybook.handybook.BuildConfig;
import com.handybook.handybook.core.constant.PrefsKey;
import com.handybook.handybook.core.event.EnvironmentUpdatedEvent;
import com.handybook.handybook.core.manager.DefaultPreferencesManager;
import com.handybook.handybook.library.util.PropertiesReader;

import org.greenrobot.eventbus.EventBus;

import java.util.Properties;

public class EnvironmentModifier {

    public static final class Environment {

        public static final String PRODUCTION = "production";
        public static final String NAMESPACE = "namespace";
        public static final String LOCAL = "local";
    }


    private static final String DEFAULT_NAMESPACE = "s";

    private final EventBus mBus;
    private final DefaultPreferencesManager mDefaultPreferencesManager;

    public EnvironmentModifier(
            Context context,
            EventBus bus,
            DefaultPreferencesManager defaultPreferencesManager
    ) {
        mBus = bus;
        mDefaultPreferencesManager = defaultPreferencesManager;

        try {
            Properties properties = PropertiesReader.getProperties(context, "override.properties");
            String environmentPrefix = properties.getProperty("environment", DEFAULT_NAMESPACE);
            environmentPrefix = mDefaultPreferencesManager.getString(
                    PrefsKey.ENVIRONMENT_PREFIX,
                    environmentPrefix
            ); // whatever is stored in prefs is higher priority

            mDefaultPreferencesManager.setString(PrefsKey.ENVIRONMENT_PREFIX, environmentPrefix);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getEnvironment() {
        String defaultEnvironment = BuildConfig.FLAVOR.equals(BaseApplication.FLAVOR_PROD) ?
                                    Environment.PRODUCTION : Environment.NAMESPACE;
        return mDefaultPreferencesManager.getString(PrefsKey.ENVIRONMENT, defaultEnvironment);
    }

    public String getEnvironmentPrefix() {
        return mDefaultPreferencesManager.getString(PrefsKey.ENVIRONMENT_PREFIX, DEFAULT_NAMESPACE);
    }

    public boolean isNamespace() {
        return Environment.NAMESPACE.equals(getEnvironment());
    }

    public boolean isProduction() {
        return Environment.PRODUCTION.equals(getEnvironment());
    }

    public boolean isLocal() {
        return Environment.LOCAL.equals(getEnvironment());
    }

    public void setEnvironment(String environment) {
        String previousEnvironment = getEnvironment();
        mDefaultPreferencesManager.setString(PrefsKey.ENVIRONMENT, environment);

        mBus.post(new EnvironmentUpdatedEvent(environment, previousEnvironment));
    }

    public void setEnvironmentPrefix(String environmentPrefix) {
        String previousEnvironmentPrefix = getEnvironmentPrefix();
        mDefaultPreferencesManager.setString(PrefsKey.ENVIRONMENT_PREFIX, environmentPrefix);

        mBus.post(new EnvironmentUpdatedEvent(environmentPrefix, previousEnvironmentPrefix));
    }

    public interface OnEnvironmentChangedListener {

        void onEnvironmentChanged(String newEnvironment, String newEnvironmentPrefix);
    }
}
