package com.handybook.handybook.booking.model;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.Map;

/**
 * Created by sng on 6/27/17.
 * This TermsOfUse class contains the list of different types of terms of use and information
 * related to it
 */

public class TermsOfUse implements Serializable {

    //Note: https://hackmd.io/s/Sk0GtZv7W

    @SerializedName("types")
    private Map<String, TermsOfUseType> mTypes;
    //This is used to pass the field name and a value of 1 when submitting a booking
    @SerializedName("checkbox_field_name")
    private String mCheckboxFieldName;

    private String mType;

    public void setType(String type) {
        mType = type;
    }

    public String getType() {
        return mType;
    }

    /**
     *
     * @return the Terms if type is set, otherwise, return null
     */
    @Nullable
    public TermsOfUseType getTermsOfUseForCurrentType() {
        return mType != null ? mTypes.get(mType) : null;
    }

    @NonNull
    public String getCheckboxFieldName() {
        return mCheckboxFieldName;
    }

    public class TermsOfUseType implements Serializable {
        @SerializedName("html")
        private String mHtml;
        @SerializedName("checkbox_enabled")
        private boolean mIsCheckboxEnabled;

        public TermsOfUseType(@NonNull String html, boolean isCheckboxEnabled) {
            mHtml = html;
            mIsCheckboxEnabled = isCheckboxEnabled;
        }

        public boolean isCheckboxEnabled() {
            return mIsCheckboxEnabled;
        }

        @NonNull
        public String getTermsInHtml() {
            return mHtml;
        }
    }
}
