package com.handybook.handybook.vegas.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class WrappedId implements Serializable{

    @SerializedName("id")
    private final long mId;

    public WrappedId(final long rewardId) {
        mId = rewardId;
    }
}
