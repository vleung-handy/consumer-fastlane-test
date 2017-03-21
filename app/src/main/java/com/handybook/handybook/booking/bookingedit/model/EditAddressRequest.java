package com.handybook.handybook.booking.bookingedit.model;

import android.support.annotation.Nullable;

import com.google.gson.annotations.SerializedName;

public class EditAddressRequest {

    @SerializedName("address1")
    private String mAddress1;
    @SerializedName("address2")
    private String mAddress2;
    @SerializedName("zipcode")
    private String mZipcode;
    @SerializedName("city")
    private String mCity;
    @SerializedName("state")
    private final String mState;

    public EditAddressRequest(
            final String address1,
            final String address2,
            final String zipcode,
            final String city,
            final String state
    ) {
        mAddress1 = address1;
        mAddress2 = address2;
        mZipcode = zipcode;
        mCity = city;
        mState = state;
    }

    @Nullable
    public String getAddress1() {
        return mAddress1;
    }

    @Nullable
    public String getAddress2() {
        return mAddress2;
    }

    @Nullable
    public String getZipcode() {
        return mZipcode;
    }

    @Nullable
    public String getCity() {
        return mCity;
    }

    @Nullable
    public String getState() {
        return mState;
    }

}
