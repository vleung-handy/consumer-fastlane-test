package com.handybook.handybook.model.response;

import com.google.gson.annotations.SerializedName;
import com.handybook.handybook.core.EditExtrasInfo;

import java.util.Map;

public class EditHoursInfoResponse
{
    @SerializedName("hours")
    private float mBaseHours;
    @SerializedName("paid_status")
    private EditExtrasInfo.PaidStatus mPaidStatus;
    @SerializedName("extras_hours")
    private float mExtrasHours;
    @SerializedName("price_table")
    private Map<String, EditExtrasInfo.PriceInfo> mPriceTable; //TODO: move priceinfo class
    @SerializedName("total_price_table")
    private Map<String, EditExtrasInfo.PriceInfo> mTotalPriceTable;
    @SerializedName("extras_price")
    private EditExtrasInfo.OptionPrice mExtrasPrice;
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

    public EditExtrasInfo.OptionPrice getExtrasPrice()
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

    public EditExtrasInfo.PaidStatus getPaidStatus() //TODO: move paidstatus class
    {
        return mPaidStatus;
    }

    public Map<String, EditExtrasInfo.PriceInfo> getTotalPriceTable()
    {
        return mTotalPriceTable;
    }

    public Map<String, EditExtrasInfo.PriceInfo> getPriceTable()
    {
        return mPriceTable;
    }


}
