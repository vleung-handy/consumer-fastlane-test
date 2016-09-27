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
    private String mFullAddress;
    @SerializedName("frequency")
    private String mFrequency;
    @SerializedName("cancel_url")
    private String mCancelUrl;
    @SerializedName("address_components")
    private Booking.Address mAddress;

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

    public String getFullAddress()
    {
        return mFullAddress;
    }

    public String getFrequency()
    {
        return mFrequency;
    }

    public String getCancelUrl()
    {
        return mCancelUrl;
    }

    public Booking.Address getAddress() { return mAddress; }

    public void setFrequency(final String frequency) { mFrequency = frequency; }

    public void setAddress(final Booking.Address address) { mAddress = address; }
}
