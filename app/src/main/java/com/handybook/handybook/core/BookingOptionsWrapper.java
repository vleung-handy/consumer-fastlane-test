package com.handybook.handybook.core;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public final class BookingOptionsWrapper
{
    @SerializedName("booking_options") private List<BookingOption> bookingOptions;
    public BookingOptionsWrapper() {}
    public final List<BookingOption> getBookingOptions()
        {
            return bookingOptions;
        }
}
