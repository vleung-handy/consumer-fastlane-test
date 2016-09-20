package com.handybook.handybook.booking.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * model from server that determines how to display an entry option to the user
 */
public class EntryMethodOption implements Serializable, Option
{
    @SerializedName("recommended")
    private boolean mRecommended;
    /**
     * true if this option should be selected by default
     */
    @SerializedName("default")
    private boolean mDefault;

    /**
     * the underlying value of this option that will be sent to the server
     */
    @SerializedName("machine_name")
    private String mMachineName;
    /**
     * the title text of this option
     */
    @SerializedName("title")
    private String mTitleText;

    /**
     * the text below the title text
     */
    @SerializedName("subtitle")
    private String mSubtitleText;

    @SerializedName("input_form")
    private InputFormDefinition mInputFormDefinition;

    public InputFormDefinition getInputFormDefinition()
    {
        return mInputFormDefinition;
    }

    public boolean isRecommended()
    {
        return mRecommended;
    }

    @Override
    public boolean isDefault()
    {
        return mDefault;
    }

    @Override
    public String getTitleText()
    {
        return mTitleText;
    }

    @Override
    public String getSubtitleText()
    {
        return mSubtitleText;
    }

    @BookingInstruction.EntryMethodType
    public String getMachineName()
    {
        return mMachineName;
    }
}
