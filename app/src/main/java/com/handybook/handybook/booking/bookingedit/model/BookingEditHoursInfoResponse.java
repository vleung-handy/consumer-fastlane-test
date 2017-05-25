package com.handybook.handybook.booking.bookingedit.model;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.gson.annotations.SerializedName;
import com.handybook.handybook.booking.model.OptionPrice;
import com.handybook.handybook.booking.model.PaidStatus;
import com.handybook.handybook.booking.model.PriceInfo;

import java.util.Map;

public class BookingEditHoursInfoResponse {

    @SerializedName("hours")
    private double mBaseHours;
    @SerializedName("hours_label")
    private String mBaseHoursLabel;
    @SerializedName("extras_hours")
    private double mExtrasHours;
    @SerializedName("extras_hours_label")
    private String mExtrasHoursLabel;
    @SerializedName("extras_price")
    private OptionPrice mExtrasPrice;
    @SerializedName("total_hours")
    private double mTotalHours;
    @SerializedName("total_hours_label")
    private String mTotalHoursLabel;
    @SerializedName("total_formatted")
    private String mTotalPriceFormattedDollars;
    @SerializedName("total_subtext")
    private String mTotalSubtext;
    @SerializedName("paid_status")
    private PaidStatus mPaidStatus;
    @SerializedName("price_table")
    private Map<String, PriceInfo> mPriceMap;
    @SerializedName("total_price_table")
    private Map<String, PriceInfo> mTotalPriceMap;
    @SerializedName("recurring")
    private boolean mIsRecurring;
    @SerializedName("has_provider")
    private boolean mHasProvider;

    public double getBaseHours() {
        return mBaseHours;
    }

    public double getExtrasHours() {
        return mExtrasHours;
    }

    @NonNull
    public OptionPrice getExtrasPrice() {
        return mExtrasPrice;
    }

    public double getTotalHours() {
        return mTotalHours;
    }

    @Nullable
    public String getTotalPriceFormattedDollars() {
        return mTotalPriceFormattedDollars;
    }

    @Nullable
    public String getTotalSubtext() {
        return mTotalSubtext;
    }

    @NonNull
    public PaidStatus getPaidStatus() {
        return mPaidStatus;
    }

    @NonNull
    public Map<String, PriceInfo> getTotalPriceMap() {
        return mTotalPriceMap;
    }

    @NonNull
    public Map<String, PriceInfo> getPriceMap() {
        return mPriceMap;
    }

    @Nullable
    public String getBaseHoursLabel() {
        return mBaseHoursLabel;
    }

    @Nullable
    public String getExtrasHoursLabel() {
        return mExtrasHoursLabel;
    }

    @Nullable
    public String getTotalHoursLabel() {
        return mTotalHoursLabel;
    }

    public boolean isRecurring() {
        return mIsRecurring;
    }

    public boolean hasProvider() {
        return mHasProvider;
    }
}
