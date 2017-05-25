package com.handybook.handybook.booking.bookingedit.model;

import com.google.gson.annotations.SerializedName;

public final class BookingEditHoursRequest {

    @SerializedName("new_hrs")
    private double mNewBaseHrs;
    @SerializedName("apply_to_recurring")
    private boolean mApplyToRecurring;

    public void setNewBaseHrs(final double newBaseHrs) {
        mNewBaseHrs = newBaseHrs;
    }

    public void setApplyToRecurring(final boolean applyToRecurring) {
        mApplyToRecurring = applyToRecurring;
    }

}
