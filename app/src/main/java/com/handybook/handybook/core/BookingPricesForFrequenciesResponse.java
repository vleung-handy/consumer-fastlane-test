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

    //this function is needed because server returns formatted strings (e.g. $20) and no explicit currency char!
    public char getCurrencyChar()
    {
        String targetString = null;
        if (monthlyPriceFormatted != null) //need these checks just in case server doesn't populate all the values
        {
            targetString = monthlyPriceFormatted;
        }
        else if (bimonthlyPriceFormatted != null)
        {
            targetString = bimonthlyPriceFormatted;
        }
        else if (weeklyPriceFormatted != null)
        {
            targetString = weeklyPriceFormatted;
        }
        if (targetString != null)
        {
            return targetString.charAt(0);
        }
        return '\0';
    }

    /*
    input format: $20
    this function is needed because discount % needs to be calculated from these prices, but server returns formatted strings!
     */
    private float getDollarAmountFromFormattedPrice(String formattedPrice)
    {
        return Float.parseFloat(formattedPrice.substring(1));
    }

    public Map<Integer, Float> getPriceMap()
    {
        Map<Integer, Float> priceMap = new HashMap<>();
        priceMap.put(1, getDollarAmountFromFormattedPrice(weeklyPriceFormatted));
        priceMap.put(2, getDollarAmountFromFormattedPrice(bimonthlyPriceFormatted));
        priceMap.put(4, getDollarAmountFromFormattedPrice(monthlyPriceFormatted));
        return priceMap;
    }

    public int getCurrentFrequency()
    {
        return currentFrequency;
    }
}
