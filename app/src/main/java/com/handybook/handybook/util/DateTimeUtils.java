package com.handybook.handybook.util;

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
    public final static SimpleDateFormat DAY_MONTH_DATE_AT_TIME_FORMATTER = new SimpleDateFormat
            ("EEE, MMM d " +
            "'@' h:mm a");
    public final static DateFormat MONTH_AND_DAY_FORMATTER = new SimpleDateFormat("MMM d");
    public final static SimpleDateFormat LOCAL_TIME_12_HOURS_FORMATTER =
            new SimpleDateFormat("hh:mm a", Locale.getDefault());


    public final static String UNIVERSAL_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ssZ";

    /**
     * Takes in a date, format, and time zone. It will convert the given date into a string in the
     * specified time zone.
     * <p/>
     * sample input:
     * date: new Date()
     * format: "h:mm aaa"
     * timeZone: America/Los_Angeles
     * <p/>
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
