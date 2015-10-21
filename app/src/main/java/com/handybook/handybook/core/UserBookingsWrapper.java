package com.handybook.handybook.core;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public final class UserBookingsWrapper
{
    @SerializedName("user_bookings")
    private List<Booking> mBookings;

    public UserBookingsWrapper()
    {
    }

    public final List<Booking> getBookings()
    {
        return mBookings;
    }
}
