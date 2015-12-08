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

    public BookingEditAddressRequest(final String address1,
                                     final String address2,
                                     final String zipcode)
    {
        mAddress1 = address1;
        mAddress2 = address2;
        mZipcode = zipcode;
    }
}
