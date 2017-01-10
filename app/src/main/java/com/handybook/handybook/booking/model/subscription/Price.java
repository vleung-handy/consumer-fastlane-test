package com.handybook.handybook.booking.model.subscription;

import java.io.Serializable;

/**
 * Contains 2 prices, the full price, and the amount due. Numbers are represented in cents
 */
public class Price implements Serializable
{
    private int mFullPrice;     //also known as the regular price
    private int mAmountDue;     //also known as the "discounted price"

    public Price(final int fullPrice, final int amountDue)
    {
        mFullPrice = fullPrice;
        mAmountDue = amountDue;
    }

    public int getFullPrice()
    {
        return mFullPrice;
    }

    public void setFullPrice(final int fullPrice)
    {
        mFullPrice = fullPrice;
    }

    public int getAmountDue()
    {
        return mAmountDue;
    }

    public void setAmountDue(final int amountDue)
    {
        mAmountDue = amountDue;
    }
}
