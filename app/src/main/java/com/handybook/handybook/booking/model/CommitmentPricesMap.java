package com.handybook.handybook.booking.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.HashMap;

public class CommitmentPricesMap extends HashMap<String, CommitmentPricesMap.CommitmentType>
{

    private static final String PRICE_KEY = "price";
    private static final String PRICE_WEEKLY_RECURRING_KEY = "weekly_recurring_price";
    private static final String PRICE_MONTHLY_RECURRING_KEY = "monthly_recurring_price";
    private static final String PRICE_BIMONTHLY_RECURRING_KEY = "bimonthly_recurring_price";
    public static final String NO_COMMITMENT_KEY = "no_commitment";

    public ArrayList<BookingPriceInfo> toPriceTable()
    {
        final ArrayList<BookingPriceInfo> priceTable = new ArrayList<>();
        HashMap<String, CommitmentRecurrenceFrequency> crf = get(NO_COMMITMENT_KEY)
                .get("0")
                .getFrequencyHashMap();
        ArrayList<String> keys = new ArrayList<>(crf.get(PRICE_KEY).getPriceItemHashMap().keySet());
        for (String hoursKey : keys)
        {

            final float hours = Float.valueOf(hoursKey);
            final float price = getPrice(PRICE_KEY, hoursKey);
            final float discountPrice = getDiscountPrice(PRICE_KEY, hoursKey);
            final float biMonthlyPrice = getPrice(PRICE_BIMONTHLY_RECURRING_KEY, hoursKey);
            final float discountBiMonthlyPrice = getDiscountPrice(
                    PRICE_BIMONTHLY_RECURRING_KEY,
                    hoursKey
            );
            final float monthlyPrice = getPrice(PRICE_MONTHLY_RECURRING_KEY, hoursKey);
            final float discountMonthlyPrice = getDiscountPrice(
                    PRICE_MONTHLY_RECURRING_KEY,
                    hoursKey
            );
            final float weeklyPrice = getPrice(PRICE_WEEKLY_RECURRING_KEY, hoursKey);
            final float discountWeeklyPrice = getDiscountPrice(
                    PRICE_WEEKLY_RECURRING_KEY,
                    hoursKey
            );
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
            final HashMap<String, CommitmentRecurrenceFrequency> crf = get(NO_COMMITMENT_KEY)
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
            final HashMap<String, CommitmentRecurrenceFrequency> crf = get(NO_COMMITMENT_KEY)
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
