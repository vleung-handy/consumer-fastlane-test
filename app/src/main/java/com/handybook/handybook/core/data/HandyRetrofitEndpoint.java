package com.handybook.handybook.core.data;

import android.content.Context;

import com.handybook.handybook.core.EnvironmentModifier;
import com.handybook.handybook.library.util.PropertiesReader;

import java.util.Properties;

import javax.inject.Inject;

import retrofit.Endpoint;

public class HandyRetrofitEndpoint implements Endpoint {

    private final EnvironmentModifier mEnvironmentModifier;

    private final String mApiEndpoint;
    private final String mApiEndpointNamespace;
    private final String mApiEndpointLocal;

    private final String mBaseUrl;
    private final String mBaseUrlNamespace;
    private final String mBaseUrlLocal;

    @Inject
    public HandyRetrofitEndpoint(Context context, EnvironmentModifier environmentModifier) {
        mEnvironmentModifier = environmentModifier;
        final Properties config = PropertiesReader.getProperties(context, "config.properties");

        mApiEndpoint = config.getProperty("api_endpoint");
        mApiEndpointNamespace = config.getProperty("api_endpoint_namespace");
        mApiEndpointLocal = config.getProperty("api_endpoint_local");

        mBaseUrl = config.getProperty("base_url");
        mBaseUrlNamespace = config.getProperty("base_url_namespace");
        mBaseUrlLocal = config.getProperty("base_url_local");
    }

    @Override
    public final String getUrl() {
        if (mEnvironmentModifier.isNamespace()) {
            return mApiEndpointNamespace.replace("#", mEnvironmentModifier.getEnvironmentPrefix());
        }
        else if (mEnvironmentModifier.isLocal()) {
            return mApiEndpointLocal.replace("#", mEnvironmentModifier.getEnvironmentPrefix());
        }
        else {
            return mApiEndpoint;
        }
    }

    @Override
    public final String getName() {
        return mEnvironmentModifier.getEnvironment();
    }
}
