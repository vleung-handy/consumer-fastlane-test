package com.handybook.handybook.core.model;

import android.support.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class ProTimeInterval implements Serializable {

    // Expected format: "2017-03-27T11:00:00Z"
    @SerializedName("interval_start")
    private String mIntervalStartTime;
    @SerializedName("interval_end")
    private String mIntervalEndTime;

    public ProTimeInterval(final String intervalStartTime, final String intervalEndTime) {
        mIntervalStartTime = intervalStartTime;
        mIntervalEndTime = intervalEndTime;
    }

    @NonNull
    public String getIntervalStartTime() {
        return mIntervalStartTime;
    }

    @NonNull
    public String getIntervalEndTime() {
        return mIntervalEndTime;
    }
}
