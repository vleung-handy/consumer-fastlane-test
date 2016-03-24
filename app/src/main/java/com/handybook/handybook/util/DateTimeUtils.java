package com.handybook.handybook.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class DateTimeUtils
{
    public final static int MILLISECONDS_IN_SECOND = 1000;
    public final static int SECONDS_IN_MINUTE = 60;
    public final static SimpleDateFormat DAY_MONTH_DATE_AT_TIME_FORMATTER = new SimpleDateFormat
            ("EEE, MMM d " +
            "'@' h:mm a");

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
        DateFormat customFormatter = new SimpleDateFormat(format);
        if (!android.text.TextUtils.isEmpty(timeZone))
        {
            customFormatter.setTimeZone(TimeZone.getTimeZone(timeZone));
        }

        return customFormatter.format(date);
    }
}
