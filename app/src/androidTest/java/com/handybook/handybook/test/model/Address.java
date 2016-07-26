package com.handybook.handybook.test.model;

public class Address
{
    private String mZipCode;
    private String mStreetAddress1;
    private String mStreetAddress2;
    public Address(String streetAddress1, String streetAddress2, String zipCode)
    {
        mStreetAddress1 = streetAddress1;
        mStreetAddress2 = streetAddress2;
        mZipCode = zipCode;
    }

    public String getZipCode()
    {
        return mZipCode;
    }

    public String getStreetAddress1()
    {
        return mStreetAddress1;
    }

    public String getStreetAddress2()
    {
        return mStreetAddress2;
    }
}
