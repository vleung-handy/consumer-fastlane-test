package com.handybook.handybook.data;

import retrofit.Endpoint;

/**
 * Created by jwilliams on 2/20/15.
 */
public interface HandyEndpoint extends Endpoint {
    static enum Environment {P, S, Q1, Q2, Q3, Q4, Q6, Q7, D1}

    Environment getEnv();

    void setEnv(Environment env);

    String getBaseUrl();

}
