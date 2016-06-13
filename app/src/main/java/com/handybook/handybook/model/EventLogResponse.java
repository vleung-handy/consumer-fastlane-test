package com.handybook.handybook.model;

import com.google.gson.annotations.SerializedName;

public class EventLogResponse
{
    @SerializedName("bundleID")
    private String mBundleId;

    public String getBundleId()
    {
        return mBundleId;
    }
}
