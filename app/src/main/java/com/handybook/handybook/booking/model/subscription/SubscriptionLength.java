package com.handybook.handybook.booking.model.subscription;

/**
 * These consists of keys such as {0, 1, 2, 3, 4} representing months
 */
public class SubscriptionLength extends SubscriptionType
{
    private boolean mIsDefault;

    public SubscriptionLength(String key, String title, boolean isDefault)
    {
        mKey = key;
        mTitle = title;
        mIsDefault = isDefault;
    }

    public boolean isDefault()
    {
        return mIsDefault;
    }
}
