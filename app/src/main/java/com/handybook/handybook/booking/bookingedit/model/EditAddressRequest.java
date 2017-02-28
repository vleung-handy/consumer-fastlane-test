package com.handybook.handybook.booking.bookingedit.model;

import com.google.gson.annotations.SerializedName;

public class EditAddressRequest {

    @SerializedName("address1")
    private String mAddress1;
    @SerializedName("address2")
    private String mAddress2;
    @SerializedName("zipcode")
    private String mZipcode;

    public EditAddressRequest(
            final String address1,
            final String address2,
            final String zipcode
    ) {
        mAddress1 = address1;
        mAddress2 = address2;
        mZipcode = zipcode;
    }

    public String getAddress1() {
        return mAddress1;
    }

    public String getAddress2() {
        return mAddress2;
    }

    public String getZipcode() {
        return mZipcode;
    }
}
