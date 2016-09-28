package com.handybook.handybook.account.model;

import com.google.gson.annotations.SerializedName;
import com.handybook.handybook.booking.model.RecurringBooking;

public class RecurringPlanWrapper
{
    @SerializedName("success")
    private boolean mSuccess;
    @SerializedName("recurring_booking")
    private RecurringBooking mRecurringBooking;

    public RecurringBooking getRecurringBooking()
    {
        return mRecurringBooking;
    }
}
