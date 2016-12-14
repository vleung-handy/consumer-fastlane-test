package com.handybook.handybook.booking.model;

import com.google.gson.annotations.SerializedName;

import java.util.HashMap;

public class CommitmentLength
{
    @SerializedName("frequency")
    private HashMap<String, CommitmentRecurrenceFrequency> mFrequencyHashMap;

    public HashMap<String, CommitmentRecurrenceFrequency> getFrequencyHashMap()
    {
        return mFrequencyHashMap;
    }
}
