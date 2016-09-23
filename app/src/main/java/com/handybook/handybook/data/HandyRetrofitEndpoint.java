package com.handybook.handybook.data;

import android.content.Context;

import com.handybook.handybook.core.EnvironmentModifier;
import com.handybook.handybook.library.util.PropertiesReader;

import java.util.Properties;

import javax.inject.Inject;

import retrofit.Endpoint;

public class HandyRetrofitEndpoint implements Endpoint
{
    private final EnvironmentModifier mEnvironmentModifier;
    private final String mApiEndpoint;
    private final String mApiEndpointInternalStaging;
    private final String mApiEndpointInternalNonStaging;
    private final String mApiEndpointLocal;
    private final String mBaseUrl;
    private final String mBaseUrlInternalStaging;
    private final String mBaseUrlInternalNonStaging;
    private final String mBaseUrlLocal;

    @Inject
    public HandyRetrofitEndpoint(Context context, EnvironmentModifier environmentModifier)
    {
        mEnvironmentModifier = environmentModifier;
        final Properties config = PropertiesReader.getProperties(context, "config.properties");
        mApiEndpoint = config.getProperty("api_endpoint");
        mApiEndpointInternalStaging = config.getProperty("api_endpoint_internal_staging");
        mApiEndpointInternalNonStaging = config.getProperty("api_endpoint_internal_nonstaging");
        mApiEndpointLocal = config.getProperty("api_endpoint_local");
        mBaseUrl = config.getProperty("base_url");
        mBaseUrlInternalStaging = config.getProperty("base_url_internal_staging");
        mBaseUrlInternalNonStaging = config.getProperty("base_url_internal_nonstaging");
        mBaseUrlLocal = config.getProperty("base_url_local");
    }

    @Override
    public final String getUrl()
    {
        if (mEnvironmentModifier.isProduction())
        {
            return mApiEndpoint;
        }
        else if(mEnvironmentModifier.isLocal())
        {
            return mApiEndpointLocal;
        }
        else if(mEnvironmentModifier.isStaging())
        {
            return mApiEndpointInternalStaging;
        }
        else
        {
            return mApiEndpointInternalNonStaging.replace("#", mEnvironmentModifier.getEnvironment());
        }
    }

    public final String getBaseUrl()
    {
        if (mEnvironmentModifier.isProduction())
        {
            return mBaseUrl;
        }
        else if(mEnvironmentModifier.isLocal())
        {
            return mBaseUrlLocal;
        }
        else if(mEnvironmentModifier.isStaging())
        {
            return mBaseUrlInternalStaging;
        }
        else
        {
            return mBaseUrlInternalNonStaging.replace("#", mEnvironmentModifier.getEnvironment());
        }
    }

    @Override
    public final String getName()
    {
        return mEnvironmentModifier.getEnvironment();
    }
}
