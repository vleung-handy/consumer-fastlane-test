package com.handybook.handybook.core;

import com.google.gson.annotations.SerializedName;

import java.util.HashMap;
import java.util.Map;

public class BookingPricesForFrequenciesResponse
{
    //server returns these prices as dollars
    @SerializedName("weekly_price_formatted")
    private String mWeeklyPriceFormatted;
    @SerializedName("bimonthly_price_formatted")
    private String mBimonthlyPriceFormatted;
    @SerializedName("monthly_price_formatted")
    private String mMonthlyPriceFormatted;
    @SerializedName("current_freq")
    private int mCurrentFrequency;

    public Map<Integer, String> getFormattedPriceMap()
    {
        Map<Integer, String> priceMap = new HashMap<>();
        priceMap.put(1, mWeeklyPriceFormatted);
        priceMap.put(2, mBimonthlyPriceFormatted);
        priceMap.put(4, mMonthlyPriceFormatted);
        return priceMap;
    }

    public int getCurrentFrequency()
    {
        return mCurrentFrequency;
    }
}
