package com.handybook.handybook.booking.model.subscription;

import java.io.Serializable;

/**
 * Just a dummy base class to hold common fields
 */
public abstract class SubscriptionType implements Serializable
{
    private String mKey;
    private String mTitle;

    public String getKey()
    {
        return mKey;
    }

    public String getTitle()
    {
        return mTitle;
    }
}
