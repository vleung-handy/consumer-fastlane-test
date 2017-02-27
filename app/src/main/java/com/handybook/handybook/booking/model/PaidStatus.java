package com.handybook.handybook.booking.model;

import com.google.gson.annotations.SerializedName;

public class PaidStatus {

    public boolean isBilled() {
        return mBilled;
    }

    public String getFutureBillDateFormatted() {
        return mFutureBillDateFormatted;
    }

    @SerializedName("billed")
    private boolean mBilled;
    @SerializedName("future_bill_date")
    private String mFutureBillDateFormatted;
}
