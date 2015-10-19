package com.handybook.handybook.data;

import android.content.Context;

import com.handybook.handybook.core.EnvironmentModifier;

import java.util.Properties;

import javax.inject.Inject;

import retrofit.Endpoint;

public final class HandyRetrofitEndpoint implements Endpoint
{
    private final EnvironmentModifier environmentModifier;
    private final String apiEndpoint;
    private final String apiEndpointInternal;
    private final String baseUrl;
    private final String baseUrlInternal;

    @Inject
    public HandyRetrofitEndpoint(Context context, EnvironmentModifier environmentModifier)
    {
        this.environmentModifier = environmentModifier;
        final Properties config = PropertiesReader.getProperties(context, "config.properties");
        apiEndpoint = config.getProperty("api_endpoint");
        apiEndpointInternal = config.getProperty("api_endpoint_internal");
        baseUrl = config.getProperty("base_url");
        baseUrlInternal = config.getProperty("base_url_internal");
    }

    @Override
    public final String getUrl()
    {
        if (environmentModifier.isProduction())
        {
            return apiEndpoint;
        }
        else
        {
            return apiEndpointInternal.replace("#", environmentModifier.getEnvironment());
        }
    }

    public final String getBaseUrl()
    {
        if (environmentModifier.isProduction())
        {
            return baseUrl;
        }
        else
        {
            return baseUrlInternal.replace("#", environmentModifier.getEnvironment());
        }
    }

    @Override
    public final String getName()
    {
        return environmentModifier.getEnvironment();
    }
}
