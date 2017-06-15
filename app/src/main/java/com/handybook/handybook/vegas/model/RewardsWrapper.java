package com.handybook.handybook.vegas.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class RewardsWrapper implements Serializable {

    @SerializedName("games")
    public VegasGame[] games;

}
