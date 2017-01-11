package com.handybook.handybook.booking.model.subscription;

/**
 * These consists of keys such as {price, weekly_recurring_price, bimonthly_recurring_price,
 * etc...}
 */
public class SubscriptionFrequency extends SubscriptionType
{
    public static final int ONCE = 0;
    public static final int WEEKLY_PRICE = 1;
    public static final int BI_WEEKLY_PRICE = 2;        //this is also known as bi monthly, don't know why
    public static final int MONTHLY_PRICE = 4;
    public static final int BI_MONTHLY_PRICE = 2;       //this is also known as biweekly, don't know why

    public static final String PRICE_KEY = "price";
    public static final String PRICE_WEEKLY_RECURRING_KEY = "weekly_recurring_price";
    public static final String PRICE_MONTHLY_RECURRING_KEY = "monthly_recurring_price";
    public static final String PRICE_BIMONTHLY_RECURRING_KEY = "bimonthly_recurring_price";


    public SubscriptionFrequency(String key, String title)
    {
        mKey = key;
        mTitle = title;
    }

    /**
     * Takes in a value of {price, weekly_recurring_price, bimonthly_recurring_price, monthly_recurring_price}
     * and returns it in the form of {0, 1, 2, 4}
     * @param key
     * @return
     */
    public static String convertFrequencyKey(String key)
    {
        if (PRICE_KEY.equals(key))
        {
            return String.valueOf(ONCE);
        }
        else if (PRICE_WEEKLY_RECURRING_KEY.equals(key))
        {
            return String.valueOf(WEEKLY_PRICE);
        }
        else if (PRICE_MONTHLY_RECURRING_KEY.equals(key))
        {
            return String.valueOf(MONTHLY_PRICE);
        }
        else if (PRICE_BIMONTHLY_RECURRING_KEY.equals(key))
        {
            return String.valueOf(BI_MONTHLY_PRICE);
        }

        //this is the default behavior. Shouldn't happen.
        return String.valueOf(ONCE);
    }
}
