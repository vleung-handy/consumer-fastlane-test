package com.handybook.handybook.booking.model;

import com.google.gson.annotations.SerializedName;

public final class BookingPriceInfo {

    @SerializedName("hours")
    private float hours;
    @SerializedName("price")
    private float mPriceDollars;
    @SerializedName("discount_price")
    private float mDiscountPriceDollars;
    @SerializedName("bimonthly_recurring_price")
    private float mBiMonthlyPriceDollars;
    @SerializedName("discount_bimonthly_recurring_price")
    private float mDiscountBiMonthlyPriceDollars;
    @SerializedName("monthly_recurring_price")
    private float mMonthlyPriceDollars;
    @SerializedName("discount_monthly_recurring_price")
    private float mDiscountMonthlyPriceDollars;
    @SerializedName("weekly_recurring_price")
    private float mWeeklyPriceDollars;
    @SerializedName("discount_weekly_recurring_price")
    private float discountWeeklyPriceDollars;

    public BookingPriceInfo(
            final float hours,
            final float mPriceDollars,
            final float mDiscountPriceDollars,
            final float mBiMonthlyPriceDollars,
            final float mDiscountBiMonthlyPriceDollars,
            final float monthlyPriceDollars,
            final float mDiscountMonthlyPriceDollars,
            final float mWeeklyPriceDollar,
            final float discountWeeklyPriceDollars
    ) {
        this.hours = hours;
        this.mPriceDollars = mPriceDollars;
        this.mDiscountPriceDollars = mDiscountPriceDollars;
        this.mBiMonthlyPriceDollars = mBiMonthlyPriceDollars;
        this.mDiscountBiMonthlyPriceDollars = mDiscountBiMonthlyPriceDollars;
        this.mMonthlyPriceDollars = monthlyPriceDollars;
        this.mDiscountMonthlyPriceDollars = mDiscountMonthlyPriceDollars;
        this.mWeeklyPriceDollars = mWeeklyPriceDollar;
        this.discountWeeklyPriceDollars = discountWeeklyPriceDollars;
    }

    final float getHours() {
        return hours;
    }

    final float getPriceDollars() {
        return mPriceDollars;
    }

    final float getBiMonthlyPriceDollars() {
        return mBiMonthlyPriceDollars;
    }

    final float getMonthlyPriceDollars() {
        return mMonthlyPriceDollars;
    }

    final float getWeeklyPriceDollars() {
        return mWeeklyPriceDollars;
    }

    final float getDiscountPriceDollars() {
        return mDiscountPriceDollars;
    }

    final float getDiscountBiMonthlyPriceDollars() {
        return mDiscountBiMonthlyPriceDollars;
    }

    final float getDiscountMonthlyPriceDollars() {
        return mDiscountMonthlyPriceDollars;
    }

    final float getDiscountWeeklyPriceDollars() {
        return discountWeeklyPriceDollars;
    }

    final float getPriceCents() {
        return mPriceDollars * 100;
    }

    final float getBiMonthlyPriceCents() {
        return mBiMonthlyPriceDollars * 100;
    }

    final float getMonthlyPriceCents() {
        return mMonthlyPriceDollars * 100;
    }

    final float getWeeklyPriceCents() {
        return mWeeklyPriceDollars * 100;
    }

    final float getDiscountPriceCents() {
        return mDiscountPriceDollars * 100;
    }

    final float getDiscountBiMonthlyPriceCents() {
        return mDiscountBiMonthlyPriceDollars * 100;
    }

    final float getDiscountMonthlyPriceCents() {
        return mDiscountMonthlyPriceDollars * 100;
    }

    final float getDiscountWeeklyPriceCents() {
        return discountWeeklyPriceDollars * 100;
    }
}
