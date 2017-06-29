package com.handybook.handybook.booking.model;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.HashMap;

@Deprecated
public class CommitmentPricesMap extends HashMap<String, CommitmentPricesMap.CommitmentType> {

    private static final String PRICE_KEY = "price";
    private static final String PRICE_WEEKLY_RECURRING_KEY = "weekly_recurring_price";
    private static final String PRICE_MONTHLY_RECURRING_KEY = "monthly_recurring_price";
    private static final String PRICE_BIMONTHLY_RECURRING_KEY = "bimonthly_recurring_price";
    private static final String NO_COMMITMENT_KEY = "no_commitment";

    /**
     *
     * @return CommitmentRecurrenceFrequency if there is only One, otherwise return null
     */
    @Nullable
    CommitmentRecurrenceFrequency getCommitmentRecurrenceFrequencyIfOnlyOne() {
        HashMap<String, CommitmentRecurrenceFrequency> crf = get(NO_COMMITMENT_KEY)
                .get("0")
                .getFrequencyHashMap();

        if(crf.size() == 1) {
            //Get the item
            return crf.get(crf.keySet().iterator().next());
        }

        return null;
    }

    ArrayList<BookingPriceInfo> toPriceTable() {
        final ArrayList<BookingPriceInfo> priceTable = new ArrayList<>();
        HashMap<String, CommitmentRecurrenceFrequency> crf = get(NO_COMMITMENT_KEY)
                .get("0")
                .getFrequencyHashMap();
        ArrayList<String> keys = new ArrayList<>(crf.get(PRICE_KEY).getPriceItemHashMap().keySet());
        for (String hoursKey : keys) {

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

    private float getPrice(final String recurrenceKey, final String hourKey) {
        float fullPriceDollars;
        try {
            final HashMap<String, CommitmentRecurrenceFrequency> crf = get(NO_COMMITMENT_KEY)
                    .get("0")
                    .getFrequencyHashMap();
            final int fullPriceCents = crf
                    .get(recurrenceKey)
                    .getPriceItemHashMap()
                    .get(hourKey)
                    .getFullPriceCents();
            fullPriceDollars = convertCentsToDollars(fullPriceCents);
        }
        catch (Exception e) {
            e.printStackTrace();
            //TODO: Refactor prices in all of the app to be in cents as Long wrapper
            fullPriceDollars = 0f;
        }
        return fullPriceDollars;
    }

    private float getDiscountPrice(final String recurrenceKey, final String hourKey) {
        float fullPriceDollars;
        try {
            final HashMap<String, CommitmentRecurrenceFrequency> crf = get(NO_COMMITMENT_KEY)
                    .get("0")
                    .getFrequencyHashMap();
            final int fullPriceCents = crf
                    .get(recurrenceKey)
                    .getPriceItemHashMap()
                    .get(hourKey)
                    .getAmountDueCents();
            fullPriceDollars = convertCentsToDollars(fullPriceCents);
        }
        catch (Exception e) {
            e.printStackTrace();
            //TODO: Refactor prices in all of the app to be in cents as Long wrapper
            fullPriceDollars = 0f;

        }
        return fullPriceDollars;
    }

    private static float convertCentsToDollars(final int fullPriceCents) {
        return fullPriceCents / 100f;
    }

    static class CommitmentType extends HashMap<String, CommitmentLength> {
    }


    static class CommitmentLength {

        @SerializedName("frequency")
        private HashMap<String, CommitmentRecurrenceFrequency> mFrequencyHashMap;

        HashMap<String, CommitmentRecurrenceFrequency> getFrequencyHashMap() {
            return mFrequencyHashMap;
        }            //TODO: Refactor prices in all of the app to be in cents as Long wrapper

    }


    static class CommitmentRecurrenceFrequency {

        @SerializedName("hours")
        private HashMap<String, CommitmentPriceItem> mPriceItemHashMap;

        @SerializedName("terms_of_use_type")
        private String mTermsOfUseType;

        HashMap<String, CommitmentPriceItem> getPriceItemHashMap() {
            return mPriceItemHashMap;
        }

        @NonNull
        String getTermsOfUseType() {
            return mTermsOfUseType;
        }
    }


    private static class CommitmentPriceItem {

        @SerializedName("full_price")
        private int mFullPriceCents;
        @SerializedName("amount_due")
        private int mAmountDueCents;

        int getFullPriceCents() {
            return mFullPriceCents;
        }

        int getAmountDueCents() {
            return mAmountDueCents;
        }
    }
}
