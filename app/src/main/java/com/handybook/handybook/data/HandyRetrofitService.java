package com.handybook.handybook.data;

import android.support.annotation.Nullable;

import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;
import com.handybook.handybook.booking.bookingedit.model.BookingEditAddressRequest;
import com.handybook.handybook.booking.bookingedit.model.BookingEditExtrasRequest;
import com.handybook.handybook.booking.bookingedit.model.BookingEditFrequencyRequest;
import com.handybook.handybook.booking.bookingedit.model.BookingEditHoursRequest;
import com.handybook.handybook.booking.bookingedit.model.BookingUpdateEntryInformationTransaction;
import com.handybook.handybook.booking.bookingedit.model.BookingUpdateNoteToProTransaction;
import com.handybook.handybook.booking.model.BookingPostInfo;
import com.handybook.handybook.booking.model.BookingRequest;
import com.handybook.handybook.booking.model.BookingTransaction;
import com.handybook.handybook.booking.model.FinalizeBookingRequestPayload;
import com.handybook.handybook.booking.rating.RateImprovementFeedback;
import com.handybook.handybook.model.request.CreateUserRequest;
import com.handybook.handybook.model.request.UpdateUserRequest;
import com.handybook.handybook.module.proteam.model.ProTeamEditWrapper;

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
    @GET("/promos")
    void getAvailableSplashPromo(
            @Query("user_id") String userId,
            @Query("displayed_promos[]") String[] displayedPromos,
            @Query("accepted_promos[]") String[] acceptedPromos,
            HandyRetrofitCallback cb
    );

    @POST("/bookings/{id}/address_update")
    void editBookingAddress(
            @Path("id") int bookingId,
            @Body BookingEditAddressRequest bookingEditAddressRequest,
            HandyRetrofitCallback cb
    );

    /**
     * @param bookingRecurringId Booking.recurringId, which is the id
     *                           associated with a recurring series
     * @param cb                 callback
     */
    @POST("/bookings/{id}/recurring_cancel_send_cancel_email")
    void sendCancelRecurringBookingEmail(
            @Path("id") int bookingRecurringId,
            @Body String empty,
            HandyRetrofitCallback cb
    );

    @GET("/app_updates")
    void getBlockedWrapper(@Query("version_code") int versionCode, HandyRetrofitCallback cb);

    @GET("/services/most_common")
    void getServicesMenu(HandyRetrofitCallback cb);

    @GET("/services")
    void getServices(HandyRetrofitCallback cb);

    @GET("/bookings/{id}/edit_extras")
    void getEditExtrasInfo(@Path("id") int bookingId, HandyRetrofitCallback cb);

    @POST("/bookings/{id}/edit_extras")
    void editServiceExtras(
            @Path("id") int bookingId,
            @Body BookingEditExtrasRequest bookingEditExtrasRequest,
            HandyRetrofitCallback cb
    );

    @GET("/quotes/new")
    void getQuoteOptions(
            @Query("service_id") int serviceId, @Query("user_id") String userId,
            HandyRetrofitCallback cb
    );

    @POST("/quotes")
    void createQuote(@Body BookingRequest req, HandyRetrofitCallback cb);

    @FormUrlEncoded
    @POST("/quotes/{quote}/select_new_time")
    void updateQuoteDate(
            @Path("quote") int quoteId, @Field("date_start") Date date,
            HandyRetrofitCallback cb
    );

    @FormUrlEncoded
    @POST("/quotes/{quote}/set_coupon")
    void applyPromo(
            @Field("coupon") String promoCode, @Path("quote") int quoteId,
            @Field("user_id") String userId, @Field("email") String email,
            HandyRetrofitCallback cb
    );

    @POST("/quotes/{quote}/remove_coupon")
    void removePromo(@Path("quote") int quoteId, @Body String empty, HandyRetrofitCallback cb);

    @POST("/quotes/{quote}/create_booking")
    void createBooking(
            @Path("quote") int quoteId,
            @Body BookingTransaction req,
            HandyRetrofitCallback cb
    );

    @GET("/bookings/zipcode_validation")
    void validateBookingZip(
            @Query("service_id") int serviceId, @Query("zipcode") String zipCode,
            @Query("user_id") String userId, @Query("entered_code") String promoCode, HandyRetrofitCallback cb
    );

    @GET("/bookings/{id}/milestones")
    void getBookingMilestones(@Path("id") String bookingId, HandyRetrofitCallback cb);

    @GET("/bookings")
    void getBookings(
            @Nullable @Query("only_bookings") String bookingType,
            HandyRetrofitCallback cb
    );

    @GET("/bookings/{bookingId}/edit_hours")
    void getEditHoursInfo(
            @Path("bookingId") int bookingId,
            HandyRetrofitCallback cb
    );

    @POST("/bookings/{bookingId}/edit_hours")
    void editBookingHours(
            @Path("bookingId") int bookingId,
            @Body BookingEditHoursRequest bookingEditHoursRequest,
            HandyRetrofitCallback cb
    );

    @GET("/bookings/{id}")
    void getBooking(
            @Path("id") String bookingId,
            HandyRetrofitCallback cb
    );

    @GET("/bookings/promo_prebooking")
    void getPreBookingPromo(@Query("code") String promoCode, HandyRetrofitCallback cb);

    //Request a specific pro for a specific booking.
    @GET("/bookings/prerate_pro_info")
    void requestPrerateProInfo(@Query("booking_id") String bookingId, HandyRetrofitCallback cb);

    //sends the customer's response to prerate_pro_info
    @POST("/bookings/{id}/rating_flow")
    void postLowRatingFeedback(
            @Path("id") String bookingId,
            @Body RateImprovementFeedback feedback,
            HandyRetrofitCallback cb
    );

    @FormUrlEncoded
    @POST("/bookings/{booking}/rate_pro")
    void ratePro(
            @Path("booking") int bookingId,
            @Field("rating_int") int rating,
            @Field("tip_amount") Integer tipAmount,
            @Field("match_preference") String proMatchPreference,
            HandyRetrofitCallback cb
    );

    @FormUrlEncoded
    @POST("/bookings/{booking}/tip")
    void tipPro(
            @Path("booking") int bookingId, @Field("tip_amount") Integer tipAmount,
            HandyRetrofitCallback cb
    );

    @POST("/bookings/{booking}/rating_flow")
    void submitProRatingDetails(
            @Path("booking") int bookingId, @Body RateProRequest req,
            HandyRetrofitCallback cb
    );

    @POST("/bookings/{booking}/after_booking_update")
    void addBookingPostInfo(
            @Path("booking") int bookingId, @Body BookingPostInfo info,
            HandyRetrofitCallback cb
    );


    @POST("/bookings/{booking}/description_update")
        //points to same endpoint as update entry info but that is because the endpoint currently does too much
    void updateBookingNoteToPro(
            @Path("booking") int bookingId,
            @Body BookingUpdateNoteToProTransaction descriptionTransaction,
            HandyRetrofitCallback cb
    );

    @POST("/bookings/{booking}/description_update")
        //points to same endpoint as update note to pro but that is because the endpoint currently does too much
    void updateBookingEntryInformation(
            @Path("booking") int bookingId,
            @Body BookingUpdateEntryInformationTransaction entryInformationTransaction,
            HandyRetrofitCallback cb
    );

    @POST("/bookings/{booking}/finalize_booking")
    void finalizeBooking(
            @Path("booking") int bookingId,
            @Body FinalizeBookingRequestPayload finalizeBookingRequestPayload,
            HandyRetrofitCallback cb
    );

    @POST("/bookings/{booking}/preferences")
    void updatePreferences(
            @Path("booking") int bookingId,
            @Body FinalizeBookingRequestPayload finalizeBookingRequestPayload,
            HandyRetrofitCallback cb
    );


    /**
     * @param bookingId
     * @param bookingEditFrequencyRequest
     * @param cb
     * @deprecated Use /recurring_bookings/{recurring_id}/edit_frequency instead.
     */
    @POST("/bookings/{booking}/edit_frequency")
    void updateBookingFrequency(
            @Path("booking") int bookingId,
            @Body BookingEditFrequencyRequest bookingEditFrequencyRequest,
            HandyRetrofitCallback cb
    );


    /**
     *
     * @param bookingId
     * @param cb
     * @deprecated use /recurring_bookings/{recurring_id}/edit_frequency instead
     */
    @GET("/bookings/{booking}/edit_frequency")
    void getBookingPricesForFrequencies(
            @Path("booking") int bookingId,
            HandyRetrofitCallback cb
    );

    @POST("/recurring_bookings/{recurring_id}/edit_frequency")
    void updateRecurringFrequency(
            @Path("recurring_id") String recurringId,
            @Body BookingEditFrequencyRequest bookingEditFrequencyRequest,
            HandyRetrofitCallback cb
    );

    @GET("/recurring_bookings/{recurring_id}/edit_frequency")
    void getRecurringFrequency(
            @Path("recurring_id") String recurringId,
            HandyRetrofitCallback cb
    );

    @GET("/bookings/prereschedule_info")
    void getPreRescheduleInfo(@Query("booking_id") String bookingId, HandyRetrofitCallback cb);

    @FormUrlEncoded
    @POST("/bookings/{booking}/reschedule")
    void rescheduleBooking(
            @Path("booking") String bookingId, @Field("new_date") String date,
            @Field("reschedule_all") int rescheduleAll, @Field("user_id") String userId,
            HandyRetrofitCallback cb
    );

    @GET("/bookings/precancelation_info")
    void getPreCancelationInfo(@Query("booking_id") String bookingId, HandyRetrofitCallback cb);

    @FormUrlEncoded
    @POST("/bookings/{booking}/cancel")
    void cancelBooking(
            @Path("booking") String bookingId, @Field("cancellation_reason") int reasonCode,
            @Field("user_id") String userId, HandyRetrofitCallback cb
    );

    @FormUrlEncoded
    @POST("/bookings/{booking}/cancel")
    void cancelBooking(
            @Path("booking") String bookingId, @Field("user_id") String userId, HandyRetrofitCallback cb
    );

    @GET("/bookings/{booking}/schedule_laundry")
    void getLaundryScheduleInfo(
            @Path("booking") int bookingId,
            HandyRetrofitCallback cb
    );

    @FormUrlEncoded
    @POST("/bookings/{booking}/schedule_laundry")
    void setLaundryDropOff(
            @Path("booking") int bookingId,
            @Field("date") String date, @Field("hour") int hour,
            @Field("minute") int minute, @Field("type") String type,
            HandyRetrofitCallback cb
    );

    @GET("/bookings/{booking}/add_laundry")
    void getAddLaundryInfo(
            @Path("booking") int bookingId, HandyRetrofitCallback cb
    );

    @FormUrlEncoded
    @POST("/bookings/{booking}/add_laundry")
    void addLaundry(
            @Path("booking") int bookingId, HandyRetrofitCallback cb
    );

    @GET("/recurring_bookings")
    void getRecurringBookings(HandyRetrofitCallback cb);

    @GET("/bookings/{booking_id}/geo_status")
    void getBookingGeoStatus(@Path("booking_id") String bookingId, HandyRetrofitCallback cb);

    @GET("/bookings/{booking_id}/location_status")
    void getLocationStatus(@Path("booking_id") String bookingId, HandyRetrofitCallback cb);

    @FormUrlEncoded
    @POST("/user_sessions")
    void createUserSession(
            @Field("email") String email, @Field("password") String password,
            HandyRetrofitCallback cb
    );

    @POST("/user_sessions/fb_create")
    void createUserSessionFB(@Body CreateUserRequest createUserRequest, HandyRetrofitCallback cb);

    @POST("/users")
    void createUser(@Body CreateUserRequest createUserRequest, HandyRetrofitCallback cb);

    @GET("/users/{user}")
    void getUserInfo(
            @Path("user") String userId,
            HandyRetrofitCallback cb
    );

    @GET("/users/exists")
    void getUserExists(@Query("email") String email, HandyRetrofitCallback cb);

    @PUT("/users/{user}")
    void updateUserInfo(
            @Path("user") String userId, @Body UpdateUserRequest req,
            HandyRetrofitCallback cb
    );

    @FormUrlEncoded
    @PUT("/users/{user_id}/update_credit_card")
    void updatePaymentInfo(
            @Path("user_id") String userId, @Field("stripe_token") String token,
            HandyRetrofitCallback cb
    );

    @GET("/configuration")
    void requestConfiguration(HandyRetrofitCallback cb);

    // Notification Feed
    @GET("/users/{user_id}/notifications")
    void getNotificationResultSet(
            @Path("user_id") long userId,
            @Query("count") Long count,
            @Query("since_id") Long sinceId,
            @Query("until_id") Long untilId,
            HandyRetrofitCallback cb
    );

    @GET("/password_resets/new")
    void requestPasswordReset(@Query("email") String email, HandyRetrofitCallback cb);

    //Request a list of requestable pros for this booking. Example response :
    //{requestable_jobs: [{:name=>"Jason Jones", :id=>2462}, {:name=>"FakeJake Eubank", :id=>2746}]}
    @GET("/bookings/{booking}/request_pro_info")
    void getRequestProInfo(
            @Path("booking") int bookingId,
            HandyRetrofitCallback cb
    );

    //Request a specific pro for a specific booking.
    @FormUrlEncoded
    @POST("/bookings/{booking}/request_pro")
    void requestProForBooking(
            @Path("booking") int bookingId,
            @Field("requested_pro") int requestedProId,
            @Field("fail_on_conflict") boolean failOnConflict,
            HandyRetrofitCallback cb
    );

    //Help Center Self Service Center

    @GET("/self_service/node_details")
    void getHelpInfo(
            @Query("id") String nodeId,
            @Query("booking_id") String bookingId,
            HandyRetrofitCallback cb
    );

    @GET("/self_service/booking_node_details")
    void getHelpBookingsInfo(
            @Query("id") String nodeId,
            @Query("booking_id") String bookingId,
            HandyRetrofitCallback cb
    );

    @POST("/self_service/create_case")
    void createHelpCase(@Body TypedInput body, HandyRetrofitCallback cb);

    @POST("/referrals/prepare")
    void requestPrepareReferrals(@Body String empty, HandyRetrofitCallback cb);

    @FormUrlEncoded
    @POST("/referrals/confirm")
    void requestConfirmReferral(@Field("post_guid") String guid, HandyRetrofitCallback cb);

    @GET("/referrals/claim_details")
    void requestRedemptionDetails(@Query("post_guid") String guid, HandyRetrofitCallback cb);

    @GET("/users/{user}/provider_preferences")
    void requestProTeam(
            @Path("user") String userId,
            HandyRetrofitCallback cb
    );

    @POST("/users/{user}/provider_preferences")
    void editProTeam(
            @Path("user") String userId,
            @Body ProTeamEditWrapper proTeamEditWrapper,
            HandyRetrofitCallback cb
    );

    @POST("/events")
    void postLogs(@Body JsonObject eventLogBundle, HandyRetrofitCallback cb);

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
