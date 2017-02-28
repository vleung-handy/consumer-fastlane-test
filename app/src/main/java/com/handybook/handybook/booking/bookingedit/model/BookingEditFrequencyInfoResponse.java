package com.handybook.handybook.booking.bookingedit.model;

import com.google.gson.annotations.SerializedName;

public class BookingEditFrequencyInfoResponse {

    //server returns these prices as formatted dollar amounts
    @SerializedName("weekly_price_formatted")
    private String mWeeklyPriceFormatted;
    @SerializedName("bimonthly_price_formatted")
    private String mBimonthlyPriceFormatted;
    @SerializedName("monthly_price_formatted")
    private String mMonthlyPriceFormatted;
    @SerializedName("current_freq")
    private int mCurrentFrequency;

    public BookingEditFrequencyInfoResponse() { }

    public BookingEditFrequencyInfoResponse(
            final String weeklyPriceFormatted,
            final String bimonthlyPriceFormatted,
            final String monthlyPriceFormatted,
            final int currentFrequency
    ) {
        mWeeklyPriceFormatted = weeklyPriceFormatted;
        mBimonthlyPriceFormatted = bimonthlyPriceFormatted;
        mMonthlyPriceFormatted = monthlyPriceFormatted;
        mCurrentFrequency = currentFrequency;
    }

    public String getWeeklyPriceFormatted() {
        return mWeeklyPriceFormatted;
    }

    public String getBimonthlyPriceFormatted() {
        return mBimonthlyPriceFormatted;
    }

    public String getMonthlyPriceFormatted() {
        return mMonthlyPriceFormatted;
    }

    public int getCurrentFrequency() {
        return mCurrentFrequency;
    }
}
