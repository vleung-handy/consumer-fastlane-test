package com.handybook.handybook.booking.model;


import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by odolejsi on 4/13/16.
 */
public class PeakPriceInfo implements Serializable
{
    @SerializedName("date")
    private Date mDate;
    @SerializedName("price")
    private float mPrice;
    @SerializedName("type")
    private Type mType;
    @SerializedName("price_breakdown")
    private QuotePriceBreakdown mQuotePriceBreakdown;

    public Date getDate()
    {
        return mDate;
    }

    public Type getType()
    {
        return mType;
    }

    public float getPrice()
    {
        return mPrice;
    }

    public QuotePriceBreakdown getQuotePriceBreakdown()
    {
        return mQuotePriceBreakdown;
    }

    private static class QuotePriceBreakdown implements Serializable
    {

        @SerializedName("0")
        private QuotePriceBreakdownRecurrenceOption mNonRecurring;
        @SerializedName("1")
        private QuotePriceBreakdownRecurrenceOption mWeekly;
        @SerializedName("2")
        private QuotePriceBreakdownRecurrenceOption mBiWeekly;
        @SerializedName("4")
        private QuotePriceBreakdownRecurrenceOption mQuadWeekly;

        public QuotePriceBreakdownRecurrenceOption getNonRecurring()
        {
            return mNonRecurring;
        }

        public QuotePriceBreakdownRecurrenceOption getWeekly()
        {
            return mWeekly;
        }

        public QuotePriceBreakdownRecurrenceOption getBiWeekly()
        {
            return mBiWeekly;
        }

        public QuotePriceBreakdownRecurrenceOption getQuadWeekly()
        {
            return mQuadWeekly;
        }
    }


    private static class QuotePriceBreakdownRecurrenceOption implements Serializable
    {
        @SerializedName("price")
        private float mPrice;
        @SerializedName("type")
        private Type mType;

        public float getPrice()
        {
            return mPrice;
        }

        public Type getType()
        {
            return mType;
        }
    }


    public enum Type
    {
        @SerializedName("reg-price")
        REG_PRICE,
        @SerializedName("peak-price")
        PEAK_PRICE,
        @SerializedName("disabled-price")
        DISABLED_PRICE,
        UNKNOWN
    }

}
