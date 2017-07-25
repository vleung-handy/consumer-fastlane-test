package com.handybook.handybook.core.data;

import android.content.Context;
import android.support.annotation.NonNull;

import com.handybook.handybook.core.EnvironmentModifier;
import com.handybook.handybook.library.util.PropertiesReader;

import java.util.Properties;

public class EnvironmentModifierUrlResolver implements UrlResolver {

    private Context mContext;
    private EnvironmentModifier mEnvironmentModifier;

    private String mApiEndpoint;
    private String mApiEndpointNamespace;
    private String mApiEndpointLocal;

    private String mBaseUrl;
    private String mBaseUrlNamespace;
    private String mBaseUrlLocal;

    public EnvironmentModifierUrlResolver(
            @NonNull EnvironmentModifier environmentModifier,
            @NonNull Context context
    ) {

        mEnvironmentModifier = environmentModifier;
        mContext = context;

        Properties properties = PropertiesReader.getProperties(mContext, "config.properties");

        mApiEndpoint = properties.getProperty("api_endpoint");
        mApiEndpointNamespace = properties.getProperty("api_endpoint_namespace");
        mApiEndpointLocal = properties.getProperty("api_endpoint_local");

        mBaseUrl = properties.getProperty("base_url");
        mBaseUrlNamespace = properties.getProperty("base_url_namespace");
        mBaseUrlLocal = properties.getProperty("base_url_local");
    }

    @Override
    public String getApiUrl() {
        String apiUrl;
        if (mEnvironmentModifier.isNamespace()) {
            apiUrl = mApiEndpointNamespace.replace(
                    "#",
                    mEnvironmentModifier.getEnvironmentPrefix()
            );
        }
        else if (mEnvironmentModifier.isLocal()) {
            apiUrl = mApiEndpointLocal.replace("#", mEnvironmentModifier.getEnvironmentPrefix());
        }
        else {
            apiUrl = mApiEndpoint;
        }
        return apiUrl;
    }

    @Override
    public final String getHostUrl() {
        if (mEnvironmentModifier.isNamespace()) {
            return mBaseUrlNamespace.replace("#", mEnvironmentModifier.getEnvironmentPrefix());
        }
        else if (mEnvironmentModifier.isLocal()) {
            return mBaseUrlLocal.replace("#", mEnvironmentModifier.getEnvironmentPrefix());
        }
        else {
            return mBaseUrl;
        }
    }

    public final String getName() {
        return mEnvironmentModifier.getEnvironment();
    }
}