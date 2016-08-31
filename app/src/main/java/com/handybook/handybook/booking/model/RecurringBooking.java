package com.handybook.handybook.booking.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.Date;

public class RecurringBooking implements Serializable
{
    @SerializedName("id")
    private int mId;
    @SerializedName("hashed")
    private String mHashed;
    @SerializedName("hours")
    private float hours;
    @SerializedName("next_booking_id")
    private int mNextBookingId;
    @SerializedName("next_booking_date")
    private Date mNextBookingDate;
    @SerializedName("address")
    private String mAddress;
    @SerializedName("frequency")
    private String mFrequency;
    @SerializedName("cancel_url")
    private String mCancelUrl;

    public int getId()
    {
        return mId;
    }

    public String getHashed()
    {
        return mHashed;
    }

    public float getHours()
    {
        return hours;
    }

    public int getNextBookingId()
    {
        return mNextBookingId;
    }

    public Date getNextBookingDate()
    {
        return mNextBookingDate;
    }

    public String getAddress()
    {
        return mAddress;
    }

    public String getFrequency()
    {
        return mFrequency;
    }

    public String getCancelUrl()
    {
        return mCancelUrl;
    }
}
