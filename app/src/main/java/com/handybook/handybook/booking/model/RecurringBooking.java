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
    private float mHours;
    @SerializedName("next_booking_id")
    private int mNextBookingId;
    @SerializedName("next_booking_date")
    private Date mNextBookingDate;
    @SerializedName("address")
    private String mFullAddress;
    @SerializedName("frequency")
    private String mFrequency;
    @SerializedName("frequency_value")
    private int mFrequencyValue;
    @SerializedName("cancel_url")
    private String mCancelUrl;
    @SerializedName("address_components")
    private Booking.Address mAddress;

    public RecurringBooking() { }

    public RecurringBooking(
            final int id,
            final String hashed,
            final float hours,
            final int nextBookingId,
            final Date nextBookingDate,
            final String fullAddress,
            final String frequency,
            final int frequencyValue,
            final String cancelUrl,
            final Booking.Address address
    )
    {
        mId = id;
        mHashed = hashed;
        mHours = hours;
        mNextBookingId = nextBookingId;
        mNextBookingDate = nextBookingDate;
        mFullAddress = fullAddress;
        mFrequency = frequency;
        mFrequencyValue = frequencyValue;
        mCancelUrl = cancelUrl;
        mAddress = address;
    }

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
        return mHours;
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

    public int getFrequencyValue() { return mFrequencyValue; }

    public String getCancelUrl()
    {
        return mCancelUrl;
    }

    public Booking.Address getAddress() { return mAddress; }

    public void setFrequency(final String frequency) { mFrequency = frequency; }

    public void setFrequencyValue(final int frequencyValue) { mFrequencyValue = frequencyValue; }

    public void setAddress(final Booking.Address address) { mAddress = address; }
}
