package com.handybook.handybook.model.request;

import com.google.gson.annotations.SerializedName;

public class BookingEditAddressRequest
{
    @SerializedName("address1")
    private String mAddress1;
    @SerializedName("address2")
    private String mAddress2;
    @SerializedName("zipcode")
    private String mZipcode;

    public void setAddress1(final String address1)
    {
        mAddress1 = address1;
    }

    public void setAddress2(final String address2)
    {
        mAddress2 = address2;
    }

    public void setZipcode(final String zipcode)
    {
        mZipcode = zipcode;
    }
}
