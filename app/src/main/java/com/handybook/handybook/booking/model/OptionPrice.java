package com.handybook.handybook.booking.model;

import com.google.gson.annotations.SerializedName;

public final class OptionPrice {

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
