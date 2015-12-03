package com.handybook.handybook.core;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class BookingOptionsWrapper
{
    @SerializedName("booking_options")
    private List<BookingOption> mBookingOptions;

    public BookingOptionsWrapper() {}

    public List<BookingOption> getBookingOptions()
    {
        return mBookingOptions;
    }
}
