package com.handybook.handybook.booking.model;

import com.google.gson.annotations.SerializedName;

public final class BookingEditHoursRequest
{
    @SerializedName("new_hrs")
    private float mNewBaseHrs;
    @SerializedName("apply_to_recurring")
    private boolean mApplyToRecurring;

    public void setNewBaseHrs(final float newBaseHrs)
    {
        mNewBaseHrs = newBaseHrs;
    }

    public void setApplyToRecurring(final boolean applyToRecurring)
    {
        mApplyToRecurring = applyToRecurring;
    }

}
