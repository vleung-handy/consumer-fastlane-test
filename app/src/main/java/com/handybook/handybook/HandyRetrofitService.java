package com.handybook.handybook;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

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

    @GET("/bookings/zipcode_validation")
    void validateBookingZip(@Query("service_id") int serviceId, @Query("zipcode") String zipCode, HandyRetrofitCallback cb);

    @GET("/bookings")
    void getBookings(@Query("auth_token") String authToken, HandyRetrofitCallback cb);

    @GET("/bookings/new")
    void getBookingOptions(@Query("service_id") int serviceId, @Query("user_id") String userId,
                           HandyRetrofitCallback cb);

    @POST("/bookings")
    void createBooking(@Body BookingCreateRequest req, HandyRetrofitCallback cb);

    @FormUrlEncoded
    @POST("/bookings/{booking}/set_new_date")
    void updateBookingDate(@Path("booking") int bookingId, @Field("date_start") Date date,
                           HandyRetrofitCallback cb);

    @POST("/transactions")
    void completeBooking(@Body BookingTransaction req, HandyRetrofitCallback cb);

    @POST("/bookings/{booking}/after_booking_update")
    void addBookingPostInfo(@Path("booking") int bookingId, @Body BookingPostInfo info,
                            HandyRetrofitCallback cb);

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

    @GET("/users/dont_look_at_this")
    void getUserInfo(@Query("email")String email, HandyRetrofitCallback cb);

    @PUT("/users/{user}")
    void updateUserInfo(@Path("user") String userId, @Body UserUpdateRequest req, HandyRetrofitCallback cb);

    @GET("/password_resets/new")
    void requestPasswordReset(@Query("email") String email, HandyRetrofitCallback cb);

    static final class UserUpdateRequest {
        @SerializedName("user") private User user;
        @SerializedName("auth_token") private String authToken;

        UserUpdateRequest(final User user, final String authToken) {
            this.user = user;
            this.authToken = authToken;
        }
    }

    static final class BookingCreateRequest {
        @SerializedName("booking") private BookingRequest bookingRequest;

        BookingCreateRequest(final BookingRequest bookingRequest) {
            this.bookingRequest = bookingRequest;
        }
    }
}
