package com.handybook.handybook.booking.model.subscription;

import java.io.Serializable;

/**
 * Just a dummy base class to hold common fields
 */
public abstract class SubscriptionType implements Serializable
{
    protected String mKey;
    protected String mTitle;
    protected boolean mIsDefault;

    public String getKey()
    {
        return mKey;
    }

    public String getTitle()
    {
        return mTitle;
    }

    public boolean isDefault()
    {
        return mIsDefault;
    }
}
