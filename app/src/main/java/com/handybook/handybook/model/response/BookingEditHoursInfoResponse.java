package com.handybook.handybook.model.response;

import com.google.gson.annotations.SerializedName;

import java.util.Map;

public class BookingEditHoursInfoResponse
{
    @SerializedName("hours")
    private float mBaseHours;
    @SerializedName("paid_status")
    private BookingEditExtrasInfoResponse.PaidStatus mPaidStatus;
    @SerializedName("extras_hours")
    private float mExtrasHours;
    @SerializedName("price_table")
    private Map<String, BookingEditExtrasInfoResponse.PriceInfo> mPriceMap; //TODO: move priceinfo class
    @SerializedName("total_price_table")
    private Map<String, BookingEditExtrasInfoResponse.PriceInfo> mTotalPriceMap;
    @SerializedName("extras_price")
    private BookingEditExtrasInfoResponse.OptionPrice mExtrasPrice;
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

    public BookingEditExtrasInfoResponse.OptionPrice getExtrasPrice()
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

    public BookingEditExtrasInfoResponse.PaidStatus getPaidStatus() //TODO: move paidstatus class
    {
        return mPaidStatus;
    }

    public Map<String, BookingEditExtrasInfoResponse.PriceInfo> getTotalPriceMap()
    {
        return mTotalPriceMap;
    }

    public Map<String, BookingEditExtrasInfoResponse.PriceInfo> getPriceMap()
    {
        return mPriceMap;
    }


}
