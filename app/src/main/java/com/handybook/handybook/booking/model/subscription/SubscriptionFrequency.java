package com.handybook.handybook.booking.model.subscription;

/**
 * These consists of keys such as {price, weekly_recurring_price, bimonthly_recurring_price,
 * etc...}
 */
public class SubscriptionFrequency extends SubscriptionType
{
    public SubscriptionFrequency(String key, String title)
    {
        mKey = key;
        mTitle = title;
    }
}
