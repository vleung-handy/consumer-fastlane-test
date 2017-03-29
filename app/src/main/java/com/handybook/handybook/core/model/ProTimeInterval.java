package com.handybook.handybook.core.model;

import android.support.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class ProTimeInterval implements Serializable {

    // Expected format: "2017-03-27T11:00:00Z"
    @SerializedName("interval_start")
    private String mIntervalStart;
    @SerializedName("interval_end")
    private String mIntervalEnd;

    public ProTimeInterval(final String intervalStart, final String intervalEnd) {
        mIntervalStart = intervalStart;
        mIntervalEnd = intervalEnd;
    }

    @NonNull
    public String getIntervalStart() {
        return mIntervalStart;
    }

    @NonNull
    public String getIntervalEnd() {
        return mIntervalEnd;
    }
}
