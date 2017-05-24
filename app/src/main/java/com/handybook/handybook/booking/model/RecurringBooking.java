package com.handybook.handybook.booking.model;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

public class RecurringBooking implements Serializable {

    @SerializedName("id")
    private int mId;
    @SerializedName("hashed")
    private String mHashed;
    @SerializedName("hours")
    private double mHours;
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
    @SerializedName("service_name")
    private String mServiceName;
    @SerializedName("service_machine")
    private String mMachineName;
    @SerializedName("date_start")
    private Date mDateStart;
    @SerializedName("recurring_string_short")
    private String mRecurringStringShort;
    @SerializedName("can_edit_hours")
    private boolean mCanEditHours;
    @SerializedName("edit_hours_subtext")
    private String mEditHoursSubtext;
    @SerializedName("can_edit_extras")
    private boolean mCanEditExtras;
    @SerializedName("extras")
    private ArrayList<Booking.ExtraInfo> mExtras;
    @SerializedName("edit_extras_subtext")
    private String mEditExtrasSubtext;

    public RecurringBooking() { }

    public RecurringBooking(
            final int id,
            final String hashed,
            final double hours,
            final int nextBookingId,
            final Date nextBookingDate,
            final String fullAddress,
            final String frequency,
            final int frequencyValue,
            final String cancelUrl,
            final Booking.Address address
    ) {
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

    public int getId() {
        return mId;
    }

    @NonNull
    public String getHashed() {
        return mHashed;
    }

    public double getHours() {
        return mHours;
    }

    public int getNextBookingId() {
        return mNextBookingId;
    }

    @Nullable
    public Date getNextBookingDate() {
        return mNextBookingDate;
    }

    @Nullable
    public String getFullAddress() {
        return mFullAddress;
    }

    @NonNull
    public String getFrequency() {
        return mFrequency;
    }

    public int getFrequencyValue() { return mFrequencyValue; }

    @NonNull
    public String getCancelUrl() {
        return mCancelUrl;
    }

    @Nullable
    public Booking.Address getAddress() { return mAddress; }

    public void setFrequency(final String frequency) { mFrequency = frequency; }

    public void setFrequencyValue(final int frequencyValue) { mFrequencyValue = frequencyValue; }

    public void setHours(final double hours) {
        mHours = hours;
    }

    public void setAddress(final Booking.Address address) { mAddress = address; }

    @NonNull
    public String getServiceName() {
        return mServiceName;
    }

    @NonNull
    public String getMachineName() {
        return mMachineName;
    }

    @NonNull
    public Date getDateStart() {
        return mDateStart;
    }

    @NonNull
    public String getRecurringStringShort() {
        return mRecurringStringShort;
    }

    @NonNull
    public ArrayList<Booking.ExtraInfo> getExtras() {
        return mExtras == null ? new ArrayList<Booking.ExtraInfo>() : mExtras;
    }

    @NonNull
    public ArrayList<String> getExtrasLabels() {
        final ArrayList<String> extrasLabels = new ArrayList<>();
        for (Booking.ExtraInfo eExtra : getExtras()) {
            extrasLabels.add(eExtra.getLabel());
        }
        return extrasLabels;
    }

    public boolean canEditHours() {
        return mCanEditHours;
    }

    public boolean canEditExtras() {
        return mCanEditExtras;
    }

    @Nullable
    public String getEditHoursSubtext() {
        return mEditHoursSubtext;
    }

    @Nullable
    public String getEditExtrasSubtext() {
        return mEditExtrasSubtext;
    }
}
