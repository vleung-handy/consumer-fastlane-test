package com.handybook.handybook.booking.model;

import com.google.gson.annotations.SerializedName;

import java.util.HashMap;

class CommitmentRecurrenceFrequency
{

    @SerializedName("hours")
    private HashMap<String, CommitmentPriceItem> mPriceItemHashMap;

    public HashMap<String, CommitmentPriceItem> getPriceItemHashMap()
    {
        return mPriceItemHashMap;
    }
}
