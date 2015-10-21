package com.handybook.handybook.core;

import com.google.gson.annotations.SerializedName;

import java.util.HashMap;
import java.util.Map;

public class BookingPricesForFrequenciesResponse
{
    //server returns these prices as dollars
    @SerializedName("weekly_price_formatted")
    private String weeklyPriceFormatted;
    @SerializedName("bimonthly_price_formatted")
    private String bimonthlyPriceFormatted;
    @SerializedName("monthly_price_formatted")
    private String monthlyPriceFormatted;
    @SerializedName("current_freq")
    private int currentFrequency;

    public Map<Integer, String> getFormattedPriceMap()
    {
        Map<Integer, String> priceMap = new HashMap<>();
        priceMap.put(1, weeklyPriceFormatted);
        priceMap.put(2, bimonthlyPriceFormatted);
        priceMap.put(4, monthlyPriceFormatted);
        return priceMap;
    }

    public int getCurrentFrequency()
    {
        return currentFrequency;
    }
}
