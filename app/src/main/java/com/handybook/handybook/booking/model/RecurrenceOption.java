package com.handybook.handybook.booking.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * model from server that determines how to display a recurrence option to the user
 */
public class RecurrenceOption implements Serializable, Option {

    /**
     * true if this option should be initially hidden
     */
    @SerializedName("hidden")
    private boolean mHidden;

    /**
     * true if this option should be selected by default
     */
    @SerializedName("default")
    private boolean mDefault;

    /**
     * the underlying value of this option that will be sent to the server
     */
    @SerializedName("frequency")
    private Integer mFrequencyValue;

    /**
     * the title text of this option
     */
    @SerializedName("text")
    private String mText;

    /**
     * the text below the title text
     */
    @SerializedName("subtext")
    private String mSubText;

    /**
     * the price info text
     */
    @SerializedName("price_info_text")
    private String mPriceInfoText;

    @SerializedName("terms_of_use_type")
    private String mTermsOfUseType;

    public Integer getFrequencyValue() {
        return mFrequencyValue;
    }

    @Override
    public String getTitleText() {
        return mText;
    }

    @Override
    public String getSubtitleText() {
        return mSubText;
    }

    public String getPriceInfoText() {
        return mPriceInfoText;
    }

    public boolean isHidden() {
        return mHidden;
    }

    public boolean isDefault() {
        return mDefault;
    }

    public String getTermsOfUseType() {
        return mTermsOfUseType;
    }
}
