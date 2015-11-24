package com.handybook.handybook.data;

import android.support.annotation.Nullable;

import com.google.gson.annotations.SerializedName;
import com.handybook.handybook.core.BookingPostInfo;
import com.handybook.handybook.core.BookingRequest;
import com.handybook.handybook.core.BookingTransaction;
import com.handybook.handybook.core.BookingUpdateEntryInformationTransaction;
import com.handybook.handybook.core.BookingUpdateNoteToProTransaction;
import com.handybook.handybook.model.request.BookingEditExtrasRequest;
import com.handybook.handybook.model.request.BookingEditFrequencyRequest;
import com.handybook.handybook.model.request.BookingEditHoursRequest;
import com.handybook.handybook.model.request.UpdateUserRequest;

import java.util.Date;

import retrofit.http.Body;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.PUT;
import retrofit.http.Path;
import retrofit.http.Query;
import retrofit.mime.TypedInput;

public interface HandyRetrofitService
{

    @POST("/bookings/{id}/recurring_cancel_send_cancel_email")
    void sendCancelRecurringBookingEmail(@Path("id") int bookingId,
                                         HandyRetrofitCallback cb);

    @GET("/app_updates")
    void getBlockedWrapper(@Query("version_code") int versionCode, HandyRetrofitCallback cb);

    @GET("/services/most_common")
    void getServicesMenu(HandyRetrofitCallback cb);

    @GET("/services")
    void getServices(HandyRetrofitCallback cb);

    @GET("/bookings/{id}/edit_extras")
    void getEditExtrasInfo(@Path("id") int bookingId, HandyRetrofitCallback cb);

    @POST("/bookings/{id}/edit_extras")
    void editServiceExtras(@Path("id") int bookingId,
                          @Body BookingEditExtrasRequest bookingEditExtrasRequest,
                          HandyRetrofitCallback cb);

    @GET("/quotes/new")
    void getQuoteOptions(@Query("service_id") int serviceId, @Query("user_id") String userId,
                         HandyRetrofitCallback cb);

    @POST("/quotes")
    void createQuote(@Body BookingRequest req, HandyRetrofitCallback cb);

    @FormUrlEncoded
    @POST("/quotes/{quote}/select_new_time")
    void updateQuoteDate(@Path("quote") int quoteId, @Field("date_start") Date date,
                         HandyRetrofitCallback cb);

    @FormUrlEncoded
    @POST("/quotes/{quote}/set_coupon")
    void applyPromo(@Field("coupon") String promoCode, @Path("quote") int quoteId,
                    @Field("user_id") String userId, @Field("email") String email,
                    @Field("auth_token") String authToken, HandyRetrofitCallback cb);

    @POST("/quotes/{quote}/remove_coupon")
    void removePromo(@Path("quote") int quoteId, HandyRetrofitCallback cb);

    @POST("/quotes/{quote}/create_booking")
    void createBooking(@Path("quote") int quoteId, @Body BookingTransaction req, HandyRetrofitCallback cb);

    @GET("/bookings/zipcode_validation")
    void validateBookingZip(@Query("service_id") int serviceId, @Query("zipcode") String zipCode,
                            @Query("user_id") String userId, @Query("auth_token") String authToken,
                            @Query("entered_code") String promoCode, HandyRetrofitCallback cb);

    @GET("/bookings")
    void getBookings(
            @Query("auth_token") String authToken,
            @Nullable @Query("only_bookings") String bookingType,
            HandyRetrofitCallback cb
    );

    @GET("/bookings/{bookingId}/edit_hours")
    void getEditHoursInfo(@Path("bookingId") int bookingId,
                    HandyRetrofitCallback cb);

    @POST("/bookings/{bookingId}/edit_hours")
    void editBookingHours(@Path("bookingId") int bookingId,
                           @Body BookingEditHoursRequest bookingEditHoursRequest,
                           HandyRetrofitCallback cb);

    @GET("/bookings/{id}")
    void getBooking(@Path("id") String bookingId,
                    HandyRetrofitCallback cb);

    @GET("/bookings/promo_prebooking")
    void getPreBookingPromo(@Query("code") String promoCode, HandyRetrofitCallback cb);

    @FormUrlEncoded
    @POST("/bookings/{booking}/rate_pro")
    void ratePro(@Path("booking") int bookingId, @Field("rating_int") int rating,
                 @Field("tip_amount") Integer tipAmount,
                 HandyRetrofitCallback cb);

    @FormUrlEncoded
    @POST("/bookings/{booking}/tip")
    void tipPro(@Path("booking") int bookingId, @Field("tip_amount") Integer tipAmount,
                HandyRetrofitCallback cb);

    @POST("/bookings/{booking}/rating_flow")
    void submitProRatingDetails(@Path("booking") int bookingId, @Body RateProRequest req,
                                HandyRetrofitCallback cb);

    @POST("/bookings/{booking}/after_booking_update")
    void addBookingPostInfo(@Path("booking") int bookingId, @Body BookingPostInfo info,
                            HandyRetrofitCallback cb);


    @POST("/bookings/{booking}/description_update")
        //points to same endpoint as update entry info but that is because the endpoint currently does too much
    void updateBookingNoteToPro(@Path("booking") int bookingId,
                                @Body BookingUpdateNoteToProTransaction descriptionTransaction,
                                HandyRetrofitCallback cb);

    @POST("/bookings/{booking}/description_update")
        //points to same endpoint as update note to pro but that is because the endpoint currently does too much
    void updateBookingEntryInformation(@Path("booking") int bookingId,
                                       @Body BookingUpdateEntryInformationTransaction entryInformationTransaction,
                                       HandyRetrofitCallback cb);

    @POST("/bookings/{booking}/edit_frequency")
    void updateBookingFrequency(@Path("booking") int bookingId,
                                @Body BookingEditFrequencyRequest bookingEditFrequencyRequest,
                                HandyRetrofitCallback cb);

    @GET("/bookings/{booking}/edit_frequency")
    void getBookingPricesForFrequencies(@Path("booking") int bookingId,
                                        HandyRetrofitCallback cb);

    @GET("/bookings/prereschedule_info")
    void getPreRescheduleInfo(@Query("booking_id") String bookingId, HandyRetrofitCallback cb);

    @FormUrlEncoded
    @POST("/bookings/{booking}/reschedule")
    void rescheduleBooking(@Path("booking") String bookingId, @Field("new_date") String date,
                           @Field("reschedule_all") int rescheduleAll, @Field("user_id") String userId,
                           @Field("auth_token") String authToken, HandyRetrofitCallback cb);

    @GET("/bookings/precancelation_info")
    void getPreCancelationInfo(@Query("booking_id") String bookingId, HandyRetrofitCallback cb);

    @FormUrlEncoded
    @POST("/bookings/{booking}/cancel")
    void cancelBooking(@Path("booking") String bookingId, @Field("cancellation_reason") int reasonCode,
                       @Field("user_id") String userId, @Field("auth_token") String authToken,
                       HandyRetrofitCallback cb);

    @FormUrlEncoded
    @POST("/bookings/{booking}/cancel")
    void cancelBooking(@Path("booking") String bookingId, @Field("user_id") String userId,
                       @Field("auth_token") String authToken, HandyRetrofitCallback cb);

    @GET("/bookings/{booking}/schedule_laundry")
    void getLaundryScheduleInfo(@Path("booking") int bookingId, @Query("auth_token") String authToken,
                                HandyRetrofitCallback cb);

    @FormUrlEncoded
    @POST("/bookings/{booking}/schedule_laundry")
    void setLaundryDropOff(@Path("booking") int bookingId, @Field("auth_token") String authToken,
                           @Field("date") String date, @Field("hour") int hour,
                           @Field("minute") int minute, @Field("type") String type,
                           HandyRetrofitCallback cb);

    @GET("/bookings/{booking}/add_laundry")
    void getAddLaundryInfo(@Path("booking") int bookingId, @Query("auth_token") String authToken,
                           HandyRetrofitCallback cb);

    @FormUrlEncoded
    @POST("/bookings/{booking}/add_laundry")
    void addLaundry(@Path("booking") int bookingId, @Field("auth_token") String authToken,
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
    void getUserInfo(@Path("user") String userId, @Query("auth_token") String authToken,
                     HandyRetrofitCallback cb);

    @GET("/users/exists")
    void getUserExists(@Query("email") String email, HandyRetrofitCallback cb);

    @PUT("/users/{user}")
    void updateUserInfo(@Path("user") String userId, @Body UpdateUserRequest req,
                        HandyRetrofitCallback cb);

    @GET("/password_resets/new")
    void requestPasswordReset(@Query("email") String email, HandyRetrofitCallback cb);

    //Request a list of requestable pros for this booking. Example response : {requestable_jobs: [{:name=>"Jason Jones", :id=>2462}, {:name=>"FakeJake Eubank", :id=>2746}]}
    @GET("/bookings/{booking}/request_pro_info")
    void getRequestProInfo(@Path("booking") int bookingId,
                           HandyRetrofitCallback cb);

    //Request a specific pro for a specific booking.
    @POST("/bookings/{booking}/request_pro")
    void requestProForBooking(@Path("booking") int bookingId,
                              @Query("requested_pro") int requestedProId,
                              @Query("fail_on_conflict") boolean failOnConflict,
                              HandyRetrofitCallback cb);

    //Help Center Self Service Center

    @GET("/self_service/node_details")
    void getHelpInfo(@Query("id") String nodeId,
                     @Query("auth_token") String authToken,
                     @Query("booking_id") String bookingId,
                     HandyRetrofitCallback cb);

    @GET("/self_service/booking_node_details")
    void getHelpBookingsInfo(@Query("id") String nodeId,
                             @Query("auth_token") String authToken,
                             @Query("booking_id") String bookingId,
                             HandyRetrofitCallback cb);

    @POST("/self_service/create_case")
    void createHelpCase(@Body TypedInput body, HandyRetrofitCallback cb);


    final class RateProRequest
    {
        @SerializedName("positive_feedback")
        private String positiveFeedback;

        RateProRequest(final String positiveFeedback)
        {
            this.positiveFeedback = positiveFeedback;
        }
    }
}
