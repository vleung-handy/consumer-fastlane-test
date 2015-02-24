package com.handybook.handybook.data;

import com.handybook.handybook.core.BookingPostInfo;
import com.handybook.handybook.core.BookingTransaction;

import java.util.Date;

import retrofit.http.Body;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.PUT;
import retrofit.http.Path;
import retrofit.http.Query;

/**
 * Created by jwilliams on 2/23/15.
 */
public class MockHandyRetrofitService implements HandyRetrofitService {

    @GET("/services/most_common")
    public void getServicesMenu(HandyRetrofitCallback cb) {

    }

    @GET("/services")
    public void getServices(HandyRetrofitCallback cb) {

    }

    @GET("/bookings/zipcode_validation")
    public void validateBookingZip(@Query("service_id") int serviceId, @Query("zipcode") String zipCode,
                            @Query("user_id") String userId, @Query("auth_token") String authToken,
                            @Query("entered_code") String promoCode, HandyRetrofitCallback cb) {

    }

    @GET("/bookings")
    public void getBookings(@Query("auth_token") String authToken, HandyRetrofitCallback cb) {

    }

    @GET("/bookings/new")
    public void getBookingOptions(@Query("service_id") int serviceId, @Query("user_id") String userId,
                           HandyRetrofitCallback cb) {

    }

    @POST("/bookings")
    public void createBooking(@Body BookingCreateRequest req, HandyRetrofitCallback cb) {

    }

    @FormUrlEncoded
    @POST("/bookings/{booking}/set_new_date")
    public void updateBookingDate(@Path("booking") int bookingId, @Field("date_start") Date date,
                           HandyRetrofitCallback cb) {

    }

    @FormUrlEncoded
    @POST("/bookings/{booking}/set_coupon")
    public void applyPromo(@Field("coupon") String promoCode, @Path("booking") int bookingId,
                    @Field("user_id") String userId, @Field("email") String email,
                    @Field("auth_token") String authToken, HandyRetrofitCallback cb) {

    }

    @POST("/bookings/{booking}/remove_coupon")
    public void removePromo(@Path("booking") int bookingId, HandyRetrofitCallback cb) {

    }

    @GET("/bookings/promo_prebooking")
    public void getPreBookingPromo(@Query("code") String promoCode, HandyRetrofitCallback cb) {

    }

    @POST("/transactions")
    public void completeBooking(@Body BookingTransaction req, HandyRetrofitCallback cb) {

    }

    @POST("/bookings/{booking}/after_booking_update")
    public void addBookingPostInfo(@Path("booking") int bookingId, @Body BookingPostInfo info,
                            HandyRetrofitCallback cb) {
    }

    @GET("/bookings/prereschedule_info")
    public void getPreRescheduleInfo(@Query("booking_id") String bookingId, HandyRetrofitCallback cb) {

    }

    @FormUrlEncoded
    @POST("/bookings/{booking}/reschedule")
    public void rescheduleBooking(@Path("booking") String bookingId, @Field("new_date") String date,
                           @Field("reschedule_all") int rescheduleAll, @Field("user_id") String userId,
                           @Field("auth_token") String authToken, HandyRetrofitCallback cb) {

    }

    @GET("/bookings/precancelation_info")
    public void getPreCancelationInfo(@Query("booking_id") String bookingId, HandyRetrofitCallback cb) {

    }

    @FormUrlEncoded
    @POST("/bookings/{booking}/cancel")
    public void cancelBooking(@Path("booking") String bookingId, @Field("cancellation_reason") int reasonCode,
                       @Field("user_id") String userId, @Field("auth_token") String authToken,
                       HandyRetrofitCallback cb) {

    }

    @FormUrlEncoded
    @POST("/bookings/{booking}/cancel")
    public void cancelBooking(@Path("booking") String bookingId, @Field("user_id") String userId,
                       @Field("auth_token") String authToken, HandyRetrofitCallback cb) {

    }

    @FormUrlEncoded
    @POST("/user_sessions")
    public void createUserSession(@Field("email") String email, @Field("password") String password,
                           HandyRetrofitCallback cb) {

    }

    @FormUrlEncoded
    @POST("/user_sessions/fb_create")
    public void createUserSessionFB(@Field("uid") String fbid,
                             @Field("facebook_access_token") String accessToken,
                             @Field("email") String email, @Field("first_name") String firstName,
                             @Field("last_name") String lastName,
                             HandyRetrofitCallback cb) {

    }

    @GET("/users/{user}")
    public void getUserInfo(@Path("user") String userId, @Query("auth_token") String authToken,
                     HandyRetrofitCallback cb) {

    }

    @GET("/users/dont_look_at_this")
    public void getUserInfo(@Query("email")String email, HandyRetrofitCallback cb) {

    }

    @PUT("/users/{user}")
    public void updateUserInfo(@Path("user") String userId, @Body UserUpdateRequest req,
                        HandyRetrofitCallback cb) {

    }

    @GET("/password_resets/new")
    public void requestPasswordReset(@Query("email") String email, HandyRetrofitCallback cb) {

    }
}
