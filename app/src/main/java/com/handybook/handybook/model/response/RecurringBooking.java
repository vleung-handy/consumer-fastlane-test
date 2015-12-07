package com.handybook.handybook.model.response;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

public class RecurringBooking
{
    @SerializedName("id")
    private int mRecurringId;
    @SerializedName("service_name")
    private String mServiceName;
    @SerializedName("service_machine")
    private String mServiceMachine;
    @SerializedName("recurring_string_short")
    private String mRecurringStringShort;
    @SerializedName("date_start")
    private Date mNextRecurrenceDate;

    public int getRecurringId()
    {
        return mRecurringId;
    }

    public String getServiceName()
    {
        return mServiceName;
    }

    public String getServiceMachine()
    {
        return mServiceMachine;
    }

    public String getRecurringStringShort()
    {
        return mRecurringStringShort;
    }

    public Date getNextRecurrenceDate()
    {
        return mNextRecurrenceDate;
    }
}
