package com.handybook.handybook.booking.model.subscription;

import java.io.Serializable;
import java.util.Map;

/**
 * This is the prices table. It is unique to a length & frequency combination. It has meta-data
 * relating to that combination, including whether it's enabled, whether it's the default selection,
 * etc
 *
 *
 */
public class SubscriptionPrices implements Serializable
{
    private String mSubscriptionLengthKey;
    private String mSubscriptionFrequencyKey;
    private boolean mDefault;
    private boolean mEnabled;

    /**
     * the key here is the hour as a string. This is how the server returns it.
     */
    private Map<String, Price> mPrices;

    public SubscriptionPrices(
            final String subscriptionLengthKey,
            final String subscriptionFrequencyKey,
            final boolean aDefault,
            final boolean enabled,
            final Map<String, Price> prices
    )
    {
        mSubscriptionLengthKey = subscriptionLengthKey;
        mSubscriptionFrequencyKey = subscriptionFrequencyKey;
        mDefault = aDefault;
        mEnabled = enabled;
        mPrices = prices;
    }

    public Map<String, Price> getPrices()
    {
        return mPrices;
    }
}
