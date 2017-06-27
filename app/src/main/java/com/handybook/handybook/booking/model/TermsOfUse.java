package com.handybook.handybook.booking.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.Map;

/**
 * Created by sng on 6/27/17.
 * This TermsOfUse class contains the list of different types of terms of use and information
 * related to it
 */

public class TermsOfUse implements Serializable {
    private static final String KEY_STYLE_TYPE = "html";

    @SerializedName("types")
    private Map<String, Map<String, String>> mTypes;
    @SerializedName("checkbox_enabled")
    private boolean mIsCheckboxEnabled;
    //This is used to pass the field name and a value of 1 when submitting a booking
    @SerializedName("checkbox_field_name")
    private String mCheckboxFieldName;

    private String mType;

    public void setType(String type) {
        this.mType = type;
    }

    public String getType() {
        return mType;
    }

    /**
     *
     * @return the Terms if type is set, otherwise, return null
     */
    public String getTermsOfUseForCurrentType() {
        if(mType != null) {
            return getTermsForType(mType);
        }

        return mType;
    }

    public String getTermsForType(String type) {
        return mTypes.get(type).get(KEY_STYLE_TYPE);
    }

    public boolean isCheckboxEnabled() {
        return mIsCheckboxEnabled;
    }

    public String getCheckboxFieldName() {
        return mCheckboxFieldName;
    }
}
