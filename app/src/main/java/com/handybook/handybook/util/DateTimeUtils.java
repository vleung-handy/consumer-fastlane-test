package com.handybook.handybook.util;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateTimeUtils
{
    public final static SimpleDateFormat DAY_MONTH_DATE_AT_TIME_FORMATTER = new SimpleDateFormat
            ("c, MMM d " +
            "'@' h:mm a");

    //TODO: we don't really need this.
    public final static String getFormattedDate(Date date, SimpleDateFormat dateFormat)
    {
        return dateFormat.format(date);
    }

}
