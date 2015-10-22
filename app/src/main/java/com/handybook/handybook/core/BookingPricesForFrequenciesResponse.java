package com.handybook.handybook.core;

import com.google.gson.annotations.SerializedName;
import com.handybook.handybook.constant.BookingFrequency;

import java.util.HashMap;
import java.util.Map;

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

    public Map<Integer, String> getFormattedPriceMap() //map of frequency values to formatted prices
    {
        Map<Integer, String> priceMap = new HashMap<>();
        priceMap.put(BookingFrequency.WEEKLY, mWeeklyPriceFormatted);
        priceMap.put(BookingFrequency.BIMONTHLY, mBimonthlyPriceFormatted);
        priceMap.put(BookingFrequency.MONTHLY, mMonthlyPriceFormatted);
        return priceMap;
    }

    public int getCurrentFrequency()
    {
        return mCurrentFrequency;
    }
}
