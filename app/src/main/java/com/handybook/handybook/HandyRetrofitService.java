package com.handybook.handybook;

import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Query;

public interface HandyRetrofitService {
    @FormUrlEncoded
    @POST("/user_sessions")
    void createUserSession(@Field("email") String email, @Field("password") String password,
                           HandyRetrofitCallback cb);

    @FormUrlEncoded
    @POST("/user_sessions/fb_create")
    void createUserSessionFB(@Field("uid") String fbid,
                             @Field("facebook_access_token") String accessToken,
                             @Field("email") String email, @Field("first_name") String firstName,
                             @Field("last_name") String lastName,
                             HandyRetrofitCallback cb);

    @GET("/password_resets/new")
    void requestPasswordReset(@Query("email") String email, HandyRetrofitCallback cb);
}
