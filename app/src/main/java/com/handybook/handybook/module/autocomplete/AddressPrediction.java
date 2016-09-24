package com.handybook.handybook.module.autocomplete;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 */
public class AddressPrediction implements Serializable
{

    @SerializedName("full_address")
    private String mFullAddress;

    @SerializedName("street_address")
    private String mStreetAddress;

    @SerializedName("city")
    private String mCity;

    @SerializedName("state")
    private String mState;

    @SerializedName("zip")
    private String mZip;

    @SerializedName("country")
    private String mCountry;

    public String getFullAddress()
    {
        return mFullAddress;
    }

    public String getStreetAddress()
    {
        return mStreetAddress;
    }

    public String getCity()
    {
        return mCity;
    }

    public String getState()
    {
        return mState;
    }

    public String getZip()
    {
        return mZip;
    }

    public String getCountry()
    {
        return mCountry;
    }
}
