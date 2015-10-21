package com.handybook.handybook.core;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.HashMap;
import java.util.Map;

public class BookingPricesForFrequenciesResponse implements Parcelable //parcelable so that header fragment can store and then re-create this if it dies
{
    //server returns these prices as dollars
    @SerializedName("price_formatted")
    private String priceFormatted;
    @SerializedName("weekly_price_formatted")
    private String weeklyPriceFormatted;
    @SerializedName("bimonthly_price_formatted")
    private String bimonthlyPriceFormatted;
    @SerializedName("monthly_price_formatted")
    private String monthlyPriceFormatted;


    @SerializedName("discount_price_formatted")
    private String discountPriceFormatted;
    @SerializedName("weekly_discount_price_formatted")
    private String weeklyDiscountPriceFormatted;
    @SerializedName("bimonthly_discount_price_formatted")
    private String bimonthlyDiscountPriceFormatted;
    @SerializedName("monthly_discount_price_formatted")
    private String monthlyDiscountPriceFormatted;
    @SerializedName("current_freq")
    private int currentFrequency;

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator()
    {
        public BookingPricesForFrequenciesResponse createFromParcel(final Parcel in)
        {
            return new BookingPricesForFrequenciesResponse(in);
        }

        public BookingPricesForFrequenciesResponse[] newArray(final int size)
        {
            return new BookingPricesForFrequenciesResponse[size];
        }
    };

    //this function is needed because server returns formatted strings (e.g. $20) and no explicit currency char!
    public char getCurrencyChar()
    {
        String targetString = null;
        if (priceFormatted != null) //need these checks just in case server doesn't populate all the values
        {
            targetString = priceFormatted;
        }
        else if (monthlyPriceFormatted != null)
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

    public Map<Integer, float[]> getPriceMap()
    {
        Map<Integer, float[]> priceMap = new HashMap<>();
        priceMap.put(0, new float[]{getDollarAmountFromFormattedPrice(priceFormatted), getDollarAmountFromFormattedPrice(discountPriceFormatted)});
        priceMap.put(1, new float[]{getDollarAmountFromFormattedPrice(weeklyPriceFormatted), getDollarAmountFromFormattedPrice(weeklyDiscountPriceFormatted)});
        priceMap.put(2, new float[]{getDollarAmountFromFormattedPrice(bimonthlyPriceFormatted), getDollarAmountFromFormattedPrice(bimonthlyDiscountPriceFormatted)});
        priceMap.put(4, new float[]{getDollarAmountFromFormattedPrice(monthlyPriceFormatted), getDollarAmountFromFormattedPrice(monthlyDiscountPriceFormatted)});
        return priceMap;
    }

    private BookingPricesForFrequenciesResponse(final Parcel in)
    {
        final String[] stringData = new String[9];
        in.readStringArray(stringData);
        priceFormatted = stringData[0];
        weeklyPriceFormatted = stringData[1];
        bimonthlyPriceFormatted = stringData[2];
        monthlyPriceFormatted = stringData[3];
        discountPriceFormatted = stringData[4];
        weeklyDiscountPriceFormatted = stringData[5];
        bimonthlyDiscountPriceFormatted = stringData[6];
        monthlyDiscountPriceFormatted = stringData[7];
        currentFrequency = Integer.parseInt(stringData[8]);
    }

    @Override
    public int describeContents()
    {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        dest.writeStringArray(new String[]{
                priceFormatted,
                weeklyPriceFormatted,
                bimonthlyPriceFormatted,
                monthlyPriceFormatted,
                discountPriceFormatted,
                weeklyDiscountPriceFormatted,
                bimonthlyDiscountPriceFormatted,
                monthlyDiscountPriceFormatted,
                Integer.toString(currentFrequency)});
    }

    public int getCurrentFrequency()
    {
        return currentFrequency;
    }
}
