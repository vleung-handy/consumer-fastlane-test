package com.handybook.handybook.proteam.model;

import com.google.gson.annotations.SerializedName;
import com.handybook.handybook.booking.model.Provider;

import java.io.Serializable;
import java.util.ArrayList;

public class RecommendedProvidersWrapper implements Serializable {

    @SerializedName("recommended_pros")
    private ArrayList<Provider> mRecommendedProviders;

    public ArrayList<Provider> getRecommendedProviders() {
        return mRecommendedProviders;
    }
}
