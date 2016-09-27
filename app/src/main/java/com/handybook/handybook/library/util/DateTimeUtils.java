package com.handybook.handybook.library.util;

import android.support.annotation.NonNull;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class DateTimeUtils
{
    public final static int MILLISECONDS_IN_SECOND = 1000;
    public final static int SECONDS_IN_MINUTE = 60;
    public final static SimpleDateFormat SHORT_DAY_MONTH_DATE_AT_TIME_FORMATTER =
            new SimpleDateFormat("EEE, MMM d '@' h:mm a");

    public final static SimpleDateFormat DAY_MONTH_DATE_AT_TIME_FORMATTER =
            new SimpleDateFormat("EEEE, MMM d '@' h:mm a");

    public final static SimpleDateFormat DAY_MONTH_DATE_FORMATTER = new SimpleDateFormat
            ("EEEE, MMM d");
    public final static DateFormat MONTH_AND_DAY_FORMATTER = new SimpleDateFormat("MMM d");
    public final static SimpleDateFormat LOCAL_TIME_12_HOURS_FORMATTER =
            new SimpleDateFormat("hh:mma", Locale.getDefault());

    public final static SimpleDateFormat DAY_OF_WEEK_MONTH_DAY_FORMATTER =
            new SimpleDateFormat("EEEE, MMMM d", Locale.getDefault());

    public final static SimpleDateFormat DAY_OF_WEEK_SHORT_MONTH_DAY_FORMATTER =
            new SimpleDateFormat("EEEE, MMM d", Locale.getDefault());

    public final static SimpleDateFormat DAY_OF_WEEK_FORMATTER =
            new SimpleDateFormat("EEEE", Locale.getDefault());

    public final static SimpleDateFormat LOCAL_TIME_12_HOURS =
            new SimpleDateFormat("h:mmaaa", Locale.getDefault());

    public final static String UNIVERSAL_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ssZ";

    /**
     * Takes in a date, format, and time zone. It will convert the given date into a string in the
     * specified time zone.
     * <p>
     * sample input: date: new Date() format: "h:mm aaa" timeZone: America/Los_Angeles
     * <p>
     * sample output: 4:00 PM
     *
     * @param date
     * @param format
     * @param timeZone
     * @return
     */
    public static String formatDate(Date date, String format, String timeZone)
    {
        if (date == null || android.text.TextUtils.isEmpty(format))
        {
            return null;
        }

        DateFormat customFormatter = new SimpleDateFormat(format);
        if (!android.text.TextUtils.isEmpty(timeZone))
        {
            customFormatter.setTimeZone(TimeZone.getTimeZone(timeZone));
        }

        return customFormatter.format(date);
    }

    /**
     * A positive number representing the difference in seconds
     *
     * @param date1
     * @param date2
     * @return
     */
    public static long getDiffInSeconds(Date date1, Date date2)
    {
        long diff = Math.abs(date2.getTime() - date1.getTime());
        return diff / 1000 % 60;
    }

    /**
     * Day of the week/ Monday, Tuesday, Wednesday, etc.
     *
     * @param date
     * @return
     */
    public static String getDayOfWeek(Date date)
    {
        return DAY_OF_WEEK_FORMATTER.format(date);
    }

    public static String getTime(@NonNull Date date)
    {
        return LOCAL_TIME_12_HOURS.format(date).toLowerCase();
    }

    /**
     * Returns in the form of Friday, September 12
     *
     * @param date
     * @return
     */
    public static String getDayMonthDay(Date date)
    {
        return DAY_OF_WEEK_MONTH_DAY_FORMATTER.format(date);
    }

    /**
     * Returns in the form of Friday, Sept 12
     *
     * @param date
     * @return
     */
    public static String getDayShortMonthDay(Date date)
    {
        return DAY_OF_WEEK_SHORT_MONTH_DAY_FORMATTER.format(date);
    }

    public static String getTimeWithoutDate(final Date date)
    {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return getLocalTime12HoursFormatter().format(cal.getTime()).toLowerCase();
    }

    private static SimpleDateFormat getLocalTime12HoursFormatter()
    {
        LOCAL_TIME_12_HOURS_FORMATTER.setTimeZone(TimeZone.getDefault());
        return LOCAL_TIME_12_HOURS_FORMATTER;
    }
}
