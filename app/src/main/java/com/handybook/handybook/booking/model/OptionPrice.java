package com.handybook.handybook.booking.model;

import android.support.annotation.Nullable;

import com.google.gson.annotations.SerializedName;

public final class OptionPrice {

    @Nullable
    public String getFormattedPrice() {
        return mFormattedPrice;
    }

    public float getAmountDollars() {
        return mAmountDollars;
    }

    @SerializedName("formatted")
    private String mFormattedPrice;
    @SerializedName("amount")
    private float mAmountDollars;
}
