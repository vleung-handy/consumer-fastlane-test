package com.handybook.handybook.core;

import com.google.gson.GsonBuilder;
import com.google.gson.annotations.SerializedName;

/**
 * Passed in by API to denote if the application should be blocked or not
 */
public class ShouldBlockObject
{
    @SerializedName("blocked")
    private boolean blocked;

    public boolean isBlocked()
    {
        return blocked;
    }

    public static ShouldBlockObject fromJson(final String json)
    {
        return new GsonBuilder().create().fromJson(json, ShouldBlockObject.class);
    }
}
