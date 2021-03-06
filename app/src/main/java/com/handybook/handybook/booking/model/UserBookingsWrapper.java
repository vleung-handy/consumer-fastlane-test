package com.handybook.handybook.booking.model;

import com.google.gson.annotations.SerializedName;
import com.handybook.handybook.core.model.response.ErrorResponse;

import java.util.List;

public final class UserBookingsWrapper extends ErrorResponse {

    @SerializedName("user_bookings")
    private List<Booking> mBookings;
    @SerializedName("user_recurring_bookings")
    private List<RecurringBooking> mRecurringBookings;

    public UserBookingsWrapper() {
    }

    public final List<Booking> getBookings() {
        return mBookings;
    }

    public final List<RecurringBooking> getRecurringBookings() {
        return mRecurringBookings;
    }
}
