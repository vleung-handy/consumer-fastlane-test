package com.handybook.handybook.module.bookings;

import android.content.Context;

import com.handybook.handybook.R;
import com.handybook.handybook.booking.model.Booking;
import com.handybook.handybook.util.DateTimeUtils;
import com.handybook.handybook.util.TextUtils;

import java.util.Calendar;
import java.util.Date;

/**
 */
public class BookingHelper
{
    public static final String TITLE_DATE_FORMAT = "EEEE',' MMMM d";
    public static final String SUBTITLE_DATE_FORMAT = "h:mm aaa";
    public static final int MINUTES_PER_HOUR = 60;
    public static final String DURATION_FORMAT = "#.#";

    public static String getSubtitle(Booking booking, Context context)
    {
        //make sure this date is in the timezone of the booking location. This will be shown to the user
        final String start = DateTimeUtils.formatDate(booking.getStartDate(),
                SUBTITLE_DATE_FORMAT, booking.getBookingTimezone());

        Calendar cal = Calendar.getInstance();
        cal.setTime(booking.getStartDate());
        cal.add(Calendar.MINUTE, Math.round(booking.getHours() * MINUTES_PER_HOUR)); // adds booking duration
        final Date endDate = cal.getTime();

        //make sure this date is in the timezone of the booking location. This will be shown to the user
        final String end = DateTimeUtils.formatDate(endDate, SUBTITLE_DATE_FORMAT,
                booking.getBookingTimezone());

        final String duration = TextUtils.formatDecimal(booking.getHours(), DURATION_FORMAT);

        final String subtitle = context.getString(
                R.string.booking_card_row_time_and_duration,
                start,
                end,
                duration
        );
        return subtitle;
    }

    public static String getTitle(Booking booking)
    {
        return TextUtils.formatDate(booking.getStartDate(), TITLE_DATE_FORMAT);
    }
}
