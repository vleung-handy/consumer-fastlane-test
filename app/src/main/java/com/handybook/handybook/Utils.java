package com.handybook.handybook;

import android.content.Context;
import android.text.format.Time;
import android.util.TypedValue;

import java.util.Date;

public final class Utils {

    static int toDP(final float px, final Context context) {
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                px, context.getResources().getDisplayMetrics()));
    }

    static int toDP(final int px, final Context context) {
        return toDP((float)px, context);
    }

    static boolean equalDates(final Date date1, final Date date2) {
        final Time time = new Time();
        time.set(date1.getTime());

        final int thenYear = time.year;
        final int thenMonth = time.month;
        final int thenMonthDay = time.monthDay;

        time.set(date2.getTime());

        return (thenYear == time.year) && (thenMonth == time.month)
                && (thenMonthDay == time.monthDay);
    }
}
