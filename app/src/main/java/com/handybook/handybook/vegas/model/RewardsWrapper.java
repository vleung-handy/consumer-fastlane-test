package com.handybook.handybook.vegas.model;

import com.google.gson.JsonElement;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

public class RewardsWrapper implements Serializable {

    @SerializedName("games")
    private ArrayList<JsonElement> mGames;

    public ArrayList<JsonElement> getGames() {
        return mGames;
    }
}
