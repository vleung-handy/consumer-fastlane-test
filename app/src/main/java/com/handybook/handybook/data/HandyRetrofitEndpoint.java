package com.handybook.handybook.data;

import android.content.Context;

import java.util.Properties;

import javax.inject.Inject;

public final class HandyRetrofitEndpoint implements HandyEndpoint {

    private Environment env = Environment.S;
    private Context context;
    private final String apiEndpoint;
    private final String apiEndpointInternal;

    @Inject
    public HandyRetrofitEndpoint(Context context) {
        final Properties config = PropertiesReader.getProperties(context, "config.properties");
        apiEndpoint = config.getProperty("api_endpoint");
        apiEndpointInternal = config.getProperty("api_endpoint_internal");
    }

    final public Environment getEnv() {
        return env;
    }

    final public void setEnv(Environment env) {
        this.env = env;
    }

    @Override
    public final String getUrl() {
        switch (env) {
            case P:
                return apiEndpoint;

            case Q1:
                return apiEndpointInternal.replace("#", "q1");

            case Q2:
                return apiEndpointInternal.replace("#", "q2");

            case Q3:
                return apiEndpointInternal.replace("#", "q3");

            case Q4:
                return apiEndpointInternal.replace("#", "q4");

            case Q6:
                return apiEndpointInternal.replace("#", "q6");

            default:
                return apiEndpointInternal.replace("#", "s");
        }
    }

    @Override
    public final String getName() {
        switch (env) {
            case P:
                return "Prod";

            case Q1:
                return "Q1";

            case Q2:
                return "Q2";

            case Q3:
                return "Q3";

            case Q4:
                return "Q4";

            case Q6:
                return "Q6";

            default:
                return "Stage";
        }
    }
}
