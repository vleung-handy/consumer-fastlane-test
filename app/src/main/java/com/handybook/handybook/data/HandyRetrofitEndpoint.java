package com.handybook.handybook.data;

import android.content.Context;

import com.handybook.handybook.core.EnvironmentModifier;

import java.util.Properties;

import javax.inject.Inject;

import retrofit.Endpoint;

public final class HandyRetrofitEndpoint implements Endpoint
{
    private final EnvironmentModifier mEnvironmentModifier;
    private final String mApiEndpoint;
    private final String mApiEndpointInternal;
    private final String mBaseUrl;
    private final String mBaseUrlInternal;

    @Inject
    public HandyRetrofitEndpoint(Context context, EnvironmentModifier environmentModifier)
    {
        mEnvironmentModifier = environmentModifier;
        final Properties config = PropertiesReader.getProperties(context, "config.properties");
        mApiEndpoint = config.getProperty("api_endpoint");
        mApiEndpointInternal = config.getProperty("api_endpoint_internal");
        mBaseUrl = config.getProperty("base_url");
        mBaseUrlInternal = config.getProperty("base_url_internal");
    }

    @Override
    public final String getUrl()
    {
        if (mEnvironmentModifier.isProduction())
        {
            return mApiEndpoint;
        } else
        {
            return mApiEndpointInternal.replace("#", mEnvironmentModifier.getEnvironment());
        }
    }

    public final String getBaseUrl()
    {
        if (mEnvironmentModifier.isProduction())
        {
            return mBaseUrl;
        } else
        {
            return mBaseUrlInternal.replace("#", mEnvironmentModifier.getEnvironment());
        }
    }

    @Override
    public final String getName()
    {
        return mEnvironmentModifier.getEnvironment();
    }
}
