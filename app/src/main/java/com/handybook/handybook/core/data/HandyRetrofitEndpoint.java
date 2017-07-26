package com.handybook.handybook.core.data;

import android.support.annotation.NonNull;

import com.handybook.handybook.core.EnvironmentModifier;

import javax.inject.Inject;

import retrofit.Endpoint;

public class HandyRetrofitEndpoint implements Endpoint {

    private final EnvironmentModifier mEnvironmentModifier;
    private final UrlResolver mServiceUrlResolver;

    /**
     * @param environmentModifier this is only needed because getName() needs it which we
     */
    @Inject
    public HandyRetrofitEndpoint(
            @NonNull EnvironmentModifier environmentModifier,
            @NonNull UrlResolver serviceUrlResolver
    ) {
        mEnvironmentModifier = environmentModifier;
        mServiceUrlResolver = serviceUrlResolver;
    }

    @Override
    public final String getUrl() {
        return mServiceUrlResolver.getUrl();
    }

    /**
     * not sure how this is used
     */
    @Override
    public final String getName() {
        return mEnvironmentModifier.getEnvironment();
    }
}
