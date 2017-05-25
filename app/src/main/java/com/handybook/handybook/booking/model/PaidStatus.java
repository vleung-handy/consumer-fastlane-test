package com.handybook.handybook.booking.model;

import android.support.annotation.Nullable;

import com.google.gson.annotations.SerializedName;

public class PaidStatus {

    @SerializedName("future_bill_date")
    private String mFutureBillDateFormatted;

    @Nullable
    public String getFutureBillDateFormatted() {
        return mFutureBillDateFormatted;
    }

    public boolean hasFutureBillDate() {
        return mFutureBillDateFormatted != null;
    }
}
