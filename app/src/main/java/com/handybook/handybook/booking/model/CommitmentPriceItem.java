package com.handybook.handybook.booking.model;

import com.google.gson.annotations.SerializedName;

public class CommitmentPriceItem
{

    @SerializedName("full_price")
    int mFullPrice;
    @SerializedName("amount_due")
    int mAmountDue;


    public int getFullPrice()
    {
        return mFullPrice;
    }

    public int getAmountDue()
    {
        return mAmountDue;
    }
}
