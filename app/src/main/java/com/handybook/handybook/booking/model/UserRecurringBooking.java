package com.handybook.handybook.booking.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.Date;

/**
 */
public class UserRecurringBooking implements Serializable
{

    //This is the recurring series id.
    @SerializedName("id")
    private String mId;

    @SerializedName("service_name")
    private String mServiceName;

    @SerializedName("service_machine")
    private String mMachineName;

    @SerializedName("date_start")
    private Date mDateStart;

    @SerializedName("hours")
    private int mHours;

    @SerializedName("recurring_string_short")
    private String mRecurringStringShort;

    public String getId()
    {
        return mId;
    }

    public String getServiceName()
    {
        return mServiceName;
    }

    public String getMachineName()
    {
        return mMachineName;
    }

    public Date getDateStart()
    {
        return mDateStart;
    }

    public String getRecurringStringShort()
    {
        return mRecurringStringShort;
    }

    public int getHours()
    {
        return mHours;
    }
}
