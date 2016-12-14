package com.handybook.handybook.booking.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.HashMap;

public class CommitmentPricesMap extends HashMap<String, CommitmentPricesMap.CommitmentType>
{
    public ArrayList<BookingPriceInfo> toPriceTable()
    {
        final ArrayList<BookingPriceInfo> priceTable = new ArrayList<>();
        HashMap<String, CommitmentRecurrenceFrequency> crf = get("no_commitment")
                .get("0")
                .getFrequencyHashMap();
        ArrayList<String> keys = new ArrayList<>(crf.get("price").getPriceItemHashMap().keySet());
        for (String hoursKey : keys)
        {

            final float hours = Float.valueOf(hoursKey);
            final float price = getPrice("price", hoursKey);
            final float discountPrice = getDiscountPrice("price", hoursKey);
            final float biMonthlyPrice = getPrice("bimonthly_recurring_price", hoursKey);
            final float discountBiMonthlyPrice = getDiscountPrice(
                    "bimonthly_recurring_price",
                    hoursKey
            );
            final float monthlyPrice = getPrice("monthly_recurring_price", hoursKey);
            final float discountMonthlyPrice = getDiscountPrice(
                    "monthly_recurring_price",
                    hoursKey
            );
            final float weeklyPrice = getPrice("weekly_recurring_price", hoursKey);
            final float discountWeeklyPrice = getDiscountPrice("weekly_recurring_price", hoursKey);
            priceTable.add(new BookingPriceInfo(
                    hours,
                    price,
                    discountPrice,
                    biMonthlyPrice,
                    discountBiMonthlyPrice,
                    monthlyPrice,
                    discountMonthlyPrice,
                    weeklyPrice,
                    discountWeeklyPrice
            ));
        }
        return priceTable;
    }

    private float getPrice(final String recurrenceKey, final String hourKey)
    {
        float fullPriceDollars;
        try
        {
            final HashMap<String, CommitmentRecurrenceFrequency> crf = get("no_commitment")
                    .get("0")
                    .getFrequencyHashMap();
            final int fullPriceCents = crf
                    .get(recurrenceKey)
                    .getPriceItemHashMap()
                    .get(hourKey)
                    .getFullPrice();
            fullPriceDollars = fullPriceCents / 100f;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fullPriceDollars = 0f;
        }
        return fullPriceDollars;
    }

    private float getDiscountPrice(final String recurrenceKey, final String hourKey)
    {
        float fullPriceDollars;
        try
        {
            final HashMap<String, CommitmentRecurrenceFrequency> crf = get("no_commitment")
                    .get("0")
                    .getFrequencyHashMap();
            final int fullPriceCents = crf
                    .get(recurrenceKey)
                    .getPriceItemHashMap()
                    .get(hourKey)
                    .getAmountDue();
            fullPriceDollars = fullPriceCents / 100f;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fullPriceDollars = 0f;

        }
        return fullPriceDollars;
    }

    public static class CommitmentType extends HashMap<String, CommitmentLength>
    {
    }


    public static class CommitmentLength
    {
        @SerializedName("frequency")
        private HashMap<String, CommitmentRecurrenceFrequency> mFrequencyHashMap;

        public HashMap<String, CommitmentRecurrenceFrequency> getFrequencyHashMap()
        {
            return mFrequencyHashMap;
        }
    }


    static class CommitmentRecurrenceFrequency
    {

        @SerializedName("hours")
        private HashMap<String, CommitmentPriceItem> mPriceItemHashMap;

        public HashMap<String, CommitmentPriceItem> getPriceItemHashMap()
        {
            return mPriceItemHashMap;
        }
    }


    public static class CommitmentPriceItem
    {

        @SerializedName("full_price")
        int mFullPrice;
        @SerializedName("amount_due")
        int mAmountDue;


        public int getFullPrice()
        {
            return mFullPrice;
        }

        public int getAmountDue()
        {
            return mAmountDue;
        }
    }
}
