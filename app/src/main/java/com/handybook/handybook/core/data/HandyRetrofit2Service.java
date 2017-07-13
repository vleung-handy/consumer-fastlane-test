package com.handybook.handybook.core.data;

import android.support.annotation.Nullable;

import com.handybook.handybook.booking.model.UserBookingsWrapper;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface HandyRetrofit2Service {

    @GET("bookings")
    Call<UserBookingsWrapper> getBookings(
            @Nullable @Query("only_bookings") String bookingType,
            @Nullable @Query("provider_id") String providerId
    );
}
