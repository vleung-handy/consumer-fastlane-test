package com.handybook.handybook.util;

import java.text.SimpleDateFormat;

public class DateTimeUtils
{
    public final static int MILLISECONDS_IN_SECOND = 1000;
    public final static int SECONDS_IN_MINUTE = 60;
    public final static SimpleDateFormat DAY_MONTH_DATE_AT_TIME_FORMATTER = new SimpleDateFormat
            ("EEE, MMM d " +
            "'@' h:mm a");

}
