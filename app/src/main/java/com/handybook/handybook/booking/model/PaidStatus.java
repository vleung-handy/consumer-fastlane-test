package com.handybook.handybook.booking.model;

import android.support.annotation.Nullable;

import com.google.gson.annotations.SerializedName;

public class PaidStatus {

    @SerializedName("billed")
    private boolean mIsBilled;
    @SerializedName("tipped")
    private boolean mIsTipped;
    @SerializedName("future_bill_date")
    private String mFutureBillDateFormatted;
    @SerializedName("booking_charged_date")
    private String mBookingChargedDateFormatted;

    public boolean isBilled() {
        return mIsBilled;
    }

    public boolean isTipped() {
        return mIsTipped;
    }

    @Nullable
    public String getFutureBillDateFormatted() {
        return mFutureBillDateFormatted;
    }

    @Nullable
    public String getBookingChargedDateFormatted() {
        return mBookingChargedDateFormatted;
    }

    public boolean hasFutureBillDate() {
        return mFutureBillDateFormatted != null;
    }
}
