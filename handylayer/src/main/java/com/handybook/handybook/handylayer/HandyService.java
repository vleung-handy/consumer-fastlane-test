package com.handybook.handybook.handylayer;

import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.Query;

/**
 * Created by jtse on 10/31/16.
 */
public interface HandyService
{
    @GET("/layer/identity_token")
    void getLayerAuthToken(
            @Query("auth_token") String authToken,
            @Query("nonce") String nonce,
            Callback<LayerResponseWrapper> callback
    );
}
