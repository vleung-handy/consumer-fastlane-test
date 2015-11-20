package com.handybook.handybook.model.response;

import com.google.gson.annotations.SerializedName;

public final class PaidStatus
{
    public boolean isBilled()
    {
        return mBilled;
    }

    public String getFutureBillDateFormatted()
    {
        return mFutureBillDateFormatted;
    }

    @SerializedName("billed")
    private boolean mBilled;
    @SerializedName("future_bill_date")
    private String mFutureBillDateFormatted;
}
