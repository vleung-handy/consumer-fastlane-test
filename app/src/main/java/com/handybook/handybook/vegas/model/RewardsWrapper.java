package com.handybook.handybook.vegas.model;

import android.support.annotation.NonNull;

import com.google.gson.GsonBuilder;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class RewardsWrapper implements Serializable {

    @SerializedName("games")
    public VegasGame[] games;

    @NonNull
    public static RewardsWrapper fromJson(final String json) {
        return new GsonBuilder().create().fromJson(json, RewardsWrapper.class);
    }
}
