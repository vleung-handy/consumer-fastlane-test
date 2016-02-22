package com.handybook.handybook.booking.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class RecurringBookingsResponse
{
    @SerializedName("recurring_bookings")
    private List<RecurringBooking> mRecurringBookings;

    public List<RecurringBooking> getRecurringBookings()
    {
        return mRecurringBookings;
    }
}
