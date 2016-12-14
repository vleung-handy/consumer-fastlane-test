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
        for (String hour_key : keys)
        {

            final float hours = Float.valueOf(hour_key);
            final float price = crf
                    .get("price")
                    .getPriceItemHashMap()
                    .get(hour_key)
                    .getFullPrice() / 100;
            final float discountPrice = crf
                    .get("price")
                    .getPriceItemHashMap()
                    .get(hour_key)
                    .getAmountDue() / 100;
            final float biMonthlyPrice = crf
                    .get("bimonthly_recurring_price")
                    .getPriceItemHashMap()
                    .get(hour_key)
                    .getFullPrice() / 100;
            final float discountBiMonthlyPrice = crf
                    .get("bimonthly_recurring_price")
                    .getPriceItemHashMap()
                    .get(hour_key)
                    .getAmountDue() / 100;
            final float monthlyPrice = crf
                    .get("monthly_recurring_price")
                    .getPriceItemHashMap()
                    .get(hour_key)
                    .getFullPrice() / 100;
            final float discountMonthlyPrice = crf
                    .get("monthly_recurring_price")
                    .getPriceItemHashMap()
                    .get(hour_key)
                    .getAmountDue() / 100;
            final float weeklyPrice = crf
                    .get("weekly_recurring_price")
                    .getPriceItemHashMap()
                    .get(hour_key)
                    .getFullPrice() / 100;
            final float discountWeeklyPrice = crf
                    .get("weekly_recurring_price")
                    .getPriceItemHashMap()
                    .get(hour_key)
                    .getAmountDue() / 100;
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
