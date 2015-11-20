package com.handybook.handybook.model.response;

import com.google.gson.annotations.SerializedName;

import java.util.Map;

public class BookingEditHoursInfoResponse
{
    @SerializedName("hours")
    private float mBaseHours;
    @SerializedName("paid_status")
    private PaidStatus mPaidStatus;
    @SerializedName("extras_hours")
    private float mExtrasHours;
    @SerializedName("price_table")
    private Map<String, PriceInfo> mPriceMap; //TODO: move priceinfo class
    @SerializedName("total_price_table")
    private Map<String, PriceInfo> mTotalPriceMap;
    @SerializedName("extras_price")
    private OptionPrice mExtrasPrice;
    @SerializedName("total_hours")
    private float mTotalHours;
    @SerializedName("total_formatted")
    private String mTotalPriceFormattedDollars;

    public float getBaseHours()
    {
        return mBaseHours;
    }

    public float getExtrasHours()
    {
        return mExtrasHours;
    }

    public OptionPrice getExtrasPrice()
    {
        return mExtrasPrice;
    }

    public float getTotalHours()
    {
        return mTotalHours;
    }

    public String getTotalPriceFormattedDollars()
    {
        return mTotalPriceFormattedDollars;
    }

    public PaidStatus getPaidStatus() //TODO: move paidstatus class
    {
        return mPaidStatus;
    }

    public Map<String, PriceInfo> getTotalPriceMap()
    {
        return mTotalPriceMap;
    }

    public Map<String, PriceInfo> getPriceMap()
    {
        return mPriceMap;
    }


}
