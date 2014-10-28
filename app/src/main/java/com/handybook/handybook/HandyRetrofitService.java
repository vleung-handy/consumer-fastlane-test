package com.handybook.handybook;

import com.google.gson.annotations.SerializedName;

import retrofit.http.Body;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.PUT;
import retrofit.http.Path;
import retrofit.http.Query;

public interface HandyRetrofitService {

    @GET("/services/most_common")
    void getServicesMenu(HandyRetrofitCallback cb);

    @GET("/services")
    void getServices(HandyRetrofitCallback cb);

    @GET("/bookings")
    void getBookings(@Query("auth_token") String authToken, HandyRetrofitCallback cb);

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

    @GET("/users/{user}")
    void getUserInfo(@Path("user") String userId, @Query("auth_token") String authToken, HandyRetrofitCallback cb);

    @PUT("/users/{user}")
    void updateUserInfo(@Path("user") String userId, @Body UserUpdateRequest req, HandyRetrofitCallback cb);

    @GET("/password_resets/new")
    void requestPasswordReset(@Query("email") String email, HandyRetrofitCallback cb);

    static final class UserUpdateRequest {
        @SerializedName("user") private User user;
        @SerializedName("auth_token") private String authToken;

        UserUpdateRequest(User user, String authToken) {
            this.user = user;
            this.authToken = authToken;
        }
    }
}
