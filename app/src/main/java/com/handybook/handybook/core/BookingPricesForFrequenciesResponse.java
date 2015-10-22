package com.handybook.handybook.core;

import com.google.gson.annotations.SerializedName;
import com.handybook.handybook.constant.BookingFrequency;

public class BookingPricesForFrequenciesResponse
{
    //server returns these prices as formatted dollar amounts
    @SerializedName("weekly_price_formatted")
    private String mWeeklyPriceFormatted;
    @SerializedName("bimonthly_price_formatted")
    private String mBimonthlyPriceFormatted;
    @SerializedName("monthly_price_formatted")
    private String mMonthlyPriceFormatted;
    @SerializedName("current_freq")
    private int mCurrentFrequency;

    public String getFormattedPriceForFrequency(int frequency)
    {
        switch (frequency)
        {
            case BookingFrequency.WEEKLY:
                return mWeeklyPriceFormatted;
            case BookingFrequency.BIMONTHLY:
                return mBimonthlyPriceFormatted;
            case BookingFrequency.MONTHLY:
                return mMonthlyPriceFormatted;
            default:
                return null;
        }
    }

    public int getCurrentFrequency()
    {
        return mCurrentFrequency;
    }
}
