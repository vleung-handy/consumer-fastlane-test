package com.handybook.handybook.test.model;

import com.stripe.android.model.Card;

public class TestUser
{
    private String mFullName;
    private String mEmail;
    private String mPassword;
    private Address mAddress;
    private Card mCard;
    private String mPhoneNumber;
    public TestUser(String fullName, String email, String password,
                    Address address, String phoneNumber, Card card)
    {
        mFullName = fullName;
        mEmail = email;
        mPassword = password;
        mAddress = address;
        mCard = card;
        mPhoneNumber = phoneNumber;
    }

    public TestUser(String email, String password)
    {
        mEmail = email;
        mPassword = password;
    }

    public String getEmail()
    {
        return mEmail;
    }

    public String getPassword()
    {
        return mPassword;
    }

    public Address getAddress()
    {
        return mAddress;
    }

    public Card getCard()
    {
        return mCard;
    }

    public String getPhoneNumber()
    {
        return mPhoneNumber;
    }

    public String getFullName()
    {
        return mFullName;
    }
}
