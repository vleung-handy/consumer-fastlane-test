package com.handybook.handybook.booking.constant;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class BookingRecurrence //TODO: reorganize package hierarchy so that this is in a better place
{
    public final static int ONE_TIME = 0;
    public final static int WEEKLY = 1;
    public final static int BIWEEKLY = 2;
    public final static int MONTHLY = 4;

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({ONE_TIME, WEEKLY, BIWEEKLY, MONTHLY})
    public @interface BookingRecurrenceCode
    {
    }
}
