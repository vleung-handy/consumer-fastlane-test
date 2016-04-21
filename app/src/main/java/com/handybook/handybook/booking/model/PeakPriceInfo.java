package com.handybook.handybook.booking.model;


import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringDef;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Date;

/**
 * Created by odolejsi on 4/13/16.
 */
public class PeakPriceInfo implements Serializable
{
    @Retention(RetentionPolicy.SOURCE)
    @StringDef({
            RECURRENCE_INVALID,
            RECURRENCE_NONRECURRING,
            RECURRENCE_WEEKLY,
            RECURRENCE_BIWEEKLY,
            RECURRENCE_QUADWEEKLY

    })
    public @interface Recurrence {}


    public static final String RECURRENCE_INVALID = "0";
    public static final String RECURRENCE_NONRECURRING = "0";
    public static final String RECURRENCE_WEEKLY = "1";
    public static final String RECURRENCE_BIWEEKLY = "2";
    public static final String RECURRENCE_QUADWEEKLY = "4";

    @SerializedName("date")
    private Date mDate;
    @SerializedName("price")
    private float mPrice;
    @SerializedName("type")
    private Type mType;
    @SerializedName("price_breakdown")
    private QuotePriceRecurrenceBreakdown mQuotePriceRecurrenceBreakdown;

    public static
    @Recurrence
    String recurrenceFrom(int intRecurrence)
    {
        switch (intRecurrence)
        {
            case 0:
                return RECURRENCE_NONRECURRING;
            case 1:
                return RECURRENCE_WEEKLY;
            case 2:
                return RECURRENCE_BIWEEKLY;
            case 4:
                return RECURRENCE_QUADWEEKLY;
            default:
                return RECURRENCE_INVALID;
        }
    }

    public Date getDate()
    {
        return mDate;
    }

    public Type getType()
    {
        return mType;
    }

    @Nullable
    public Type getType(@NonNull @Recurrence String recurrence)
    {
        final QuotePriceBreakdownRecurrenceOption option = getQuotePriceBreakdownRecurrenceOption(recurrence);
        if (option == null)
        {
            return getType(); // Fallback
        }
        return option.getType();
    }

    public float getPrice()
    {
        return mPrice;
    }

    public float getPrice(@NonNull @Recurrence String recurrence)
    {
        final QuotePriceBreakdownRecurrenceOption option = getQuotePriceBreakdownRecurrenceOption(recurrence);
        if (option == null)
        {
            return getPrice(); // Fallback
        }
        return option.getPrice();
    }

    public QuotePriceRecurrenceBreakdown getQuotePriceRecurrenceBreakdown()
    {
        return mQuotePriceRecurrenceBreakdown;
    }

    /**
     * Returns the peak option for specified recurrence or null if it doesn't exist.
     *
     * @param recurrence for which we want the QuotePriceRecurrenceOption
     * @return QuotePriceBreakdownRecurrenceOption for Recurrence or null
     */
    @Nullable
    public QuotePriceBreakdownRecurrenceOption getQuotePriceBreakdownRecurrenceOption(
            @NonNull @Recurrence String recurrence
    )
    {
        final QuotePriceRecurrenceBreakdown breakdown = getQuotePriceRecurrenceBreakdown();
        if (breakdown == null)
        {
            return null;
        }
        switch (recurrence)
        {
            case RECURRENCE_NONRECURRING:
                return breakdown.getNonRecurring();
            case RECURRENCE_WEEKLY:
                return breakdown.getWeekly();
            case RECURRENCE_BIWEEKLY:
                return breakdown.getBiWeekly();
            case RECURRENCE_QUADWEEKLY:
                return breakdown.getQuadWeekly();
        }
        return null;
    }

    private static class QuotePriceRecurrenceBreakdown implements Serializable
    {
        @SerializedName(PeakPriceInfo.RECURRENCE_NONRECURRING)
        private QuotePriceBreakdownRecurrenceOption mNonRecurring;
        @SerializedName(PeakPriceInfo.RECURRENCE_WEEKLY)
        private QuotePriceBreakdownRecurrenceOption mWeekly;
        @SerializedName(PeakPriceInfo.RECURRENCE_BIWEEKLY)
        private QuotePriceBreakdownRecurrenceOption mBiWeekly;
        @SerializedName(PeakPriceInfo.RECURRENCE_QUADWEEKLY)
        private QuotePriceBreakdownRecurrenceOption mQuadWeekly;

        @Nullable
        public QuotePriceBreakdownRecurrenceOption getNonRecurring()
        {
            return mNonRecurring;
        }

        @Nullable
        public QuotePriceBreakdownRecurrenceOption getWeekly()
        {
            return mWeekly;
        }

        @Nullable
        public QuotePriceBreakdownRecurrenceOption getBiWeekly()
        {
            return mBiWeekly;
        }

        @Nullable
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
