package com.handybook.handybook.booking.model;

import com.google.gson.annotations.SerializedName;

public class PriceInfo
{
    @SerializedName("label")
    private String mLabel;
    @SerializedName("price_diff")
    private String mPriceDifferenceFormatted;
    @SerializedName("total_due")
    private String mTotalDueFormatted;

    public String getLabel()
    {
        return mLabel;
    }

    public String getTotalDueFormatted()
    {
        return mTotalDueFormatted;
    }

    public String getPriceDifferenceFormatted()
    {
        return mPriceDifferenceFormatted;
    }
}
