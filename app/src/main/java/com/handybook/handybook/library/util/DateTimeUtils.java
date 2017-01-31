package com.handybook.handybook.library.util;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.format.DateUtils;

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
    public final static int MINUTES_IN_HOUR = 60;

    public final static SimpleDateFormat CLOCK_FORMATTER_12HR =
            new SimpleDateFormat("h:mm a", Locale.getDefault());

    public final static SimpleDateFormat SHORT_DAY_MONTH_DATE_AT_TIME_FORMATTER =
            new SimpleDateFormat("EEE, MMM d '@' h:mm a");

    public final static SimpleDateFormat MONTH_DATE_FORMATTER =
            new SimpleDateFormat("MMMM d", Locale.getDefault());

    public final static SimpleDateFormat DAY_MONTH_DATE_AT_TIME_FORMATTER =
            new SimpleDateFormat("EEEE, MMM d '@' h:mm a");

    public final static SimpleDateFormat DAY_MONTH_DATE_FORMATTER = new SimpleDateFormat
            ("EEEE, MMM d");

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

    public final static int HOURS_IN_DAY = 24;

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

        DateFormat customFormatter = new SimpleDateFormat(format, Locale.getDefault());
        if (!android.text.TextUtils.isEmpty(timeZone))
        {
            customFormatter.setTimeZone(TimeZone.getTimeZone(timeZone));
        }

        return customFormatter.format(date);
    }

    /**
     * see formatDate(Date date, String format, String timeZone)
     * <p>
     * convenience method so that we can reuse the patterns of DateTimeUtils' static formatters
     * without a major refactor
     *
     * @param date
     * @param format   this will not be modified. a new instance of SimpleDateFormat will be created
     *                 instead of reusing this one because need to make a new instance of it to
     *                 avoid modifying the original one's timezone
     * @param timeZone
     * @return
     */
    public static String formatDate(Date date, @NonNull SimpleDateFormat format, String timeZone)
    {
        return formatDate(date, format.toPattern(), timeZone);
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

    public static Date getBeginningOfDay(Date date)
    {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        return c.getTime();
    }

    public static boolean isDateBetween(Date dateToMatch, Date date1, Date date2) {
        return date1.before(dateToMatch) && date2.after(dateToMatch);
    }

    public static int daysBetween(Date d1, Date d2)
    {
        return Math.round((d2.getTime() - d1.getTime()) / (float) (DateUtils.HOUR_IN_MILLIS * HOURS_IN_DAY));
    }

    public static String formatDateToRelativeAccuracy(final Date date)
    {
        final Calendar today = Calendar.getInstance();
        today.setTime(getBeginningOfDay(new Date()));
        final Calendar dayToCompare = Calendar.getInstance();
        dayToCompare.setTime(getBeginningOfDay(date));
        final int daysBetween = daysBetween(today.getTime(), dayToCompare.getTime());
        if (daysBetween == 0)
        {
            return formatDateTo12HourClock(date);
        }
        else if (daysBetween == -1)
        {
            return "Yesterday";
        }
        else if (daysBetween > -7)
        {
            return getDayOfWeek(date);
        }
        else
        {
            return formatMonthDate(date);
        }
    }


    @Nullable
    public static String formatMonthDate(Date date)
    {
        if (date == null) { return null; }
        return getMonthDateFormatter().format(date);
    }

    private static SimpleDateFormat getMonthDateFormatter()
    {
        MONTH_DATE_FORMATTER.setTimeZone(TimeZone.getDefault());
        return MONTH_DATE_FORMATTER;
    }

    @Nullable
    public static String formatDateTo12HourClock(Date date)
    {
        if (date == null) { return null; }
        return getClockFormatter12hr().format(date);
    }

    public static int minutesBetween(final Date date1, final Date date2)
    {
        return Math.round((date1.getTime() - date2.getTime()) / (float) (DateUtils.MINUTE_IN_MILLIS));
    }

    private static SimpleDateFormat getClockFormatter12hr()
    {
        CLOCK_FORMATTER_12HR.setTimeZone(TimeZone.getDefault());
        return CLOCK_FORMATTER_12HR;
    }
}
