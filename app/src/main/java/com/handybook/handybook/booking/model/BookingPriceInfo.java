package com.handybook.handybook.booking.model;

import com.google.gson.annotations.SerializedName;

public final class BookingPriceInfo {
    @SerializedName("hours") private float hours;
    @SerializedName("price") private float price;
    @SerializedName("discount_price") private float discountPrice;
    @SerializedName("bimonthly_recurring_price") private float biMonthlyprice;
    @SerializedName("discount_bimonthly_recurring_price") private float discountBiMonthlyprice;
    @SerializedName("monthly_recurring_price") private float monthlyPrice;
    @SerializedName("discount_monthly_recurring_price") private float discountMonthlyPrice;
    @SerializedName("weekly_recurring_price") private float weeklyPrice;
    @SerializedName("discount_weekly_recurring_price") private float discountWeeklyPrice;

    public BookingPriceInfo(
            final float hours,
            final float price,
            final float discountPrice,
            final float biMonthlyprice,
            final float discountBiMonthlyprice,
            final float monthlyPrice,
            final float discountMonthlyPrice,
            final float weeklyPrice,
            final float discountWeeklyPrice
    )
    {
        this.hours = hours;
        this.price = price;
        this.discountPrice = discountPrice;
        this.biMonthlyprice = biMonthlyprice;
        this.discountBiMonthlyprice = discountBiMonthlyprice;
        this.monthlyPrice = monthlyPrice;
        this.discountMonthlyPrice = discountMonthlyPrice;
        this.weeklyPrice = weeklyPrice;
        this.discountWeeklyPrice = discountWeeklyPrice;
    }

    final float getHours() {
        return hours;
    }

    final float getPrice() {
        return price;
    }

    final float getBiMonthlyprice() {
        return biMonthlyprice;
    }

    final float getMonthlyPrice() {
        return monthlyPrice;
    }

    final float getWeeklyPrice() {
        return weeklyPrice;
    }

    final float getDiscountPrice() {
        return discountPrice;
    }

    final float getDiscountBiMonthlyprice() {
        return discountBiMonthlyprice;
    }

    final float getDiscountMonthlyPrice() {
        return discountMonthlyPrice;
    }

    final float getDiscountWeeklyPrice() {
        return discountWeeklyPrice;
    }
}
