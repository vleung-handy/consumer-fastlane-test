package com.handybook.handybook.booking.viewmodel;

import android.content.Context;
import android.support.annotation.NonNull;

import com.handybook.handybook.R;
import com.handybook.handybook.booking.model.Booking;
import com.handybook.handybook.util.DateTimeUtils;
import com.handybook.handybook.util.TextUtils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;

public class BookingCardRowViewModel
{

    public static final String SUBTITLE_DATE_FORMAT = "h:mm aaa";
    public static final int MINUTES_PER_HOUR = 60;
    public static final String DURATION_FORMAT = "#.#";
    public static final String TITLE_DATE_FORMAT = "EEEE',' MMMM d";
    private static final long MIN_NEGATIVE_DELTA_MS_FOR_ON_MY_WAY = 60 * 60 * 1000; //an hour
    private Booking mBooking;


    private BookingCardRowViewModel(@NonNull final Booking booking)
    {
        mBooking = booking;
    }

    public static BookingCardRowViewModel from(@NonNull final Booking booking)
    {
        return new BookingCardRowViewModel(booking);
    }

    public String getTitle()
    {
        return TextUtils.formatDate(mBooking.getStartDate(), TITLE_DATE_FORMAT);
    }

    public String getSubtitle(@NonNull Context context)
    {
        //make sure this date is in the timezone of the booking location. This will be shown to the user
        final String start = DateTimeUtils.formatDate(mBooking.getStartDate(),
                SUBTITLE_DATE_FORMAT, mBooking.getBookingTimezone());

        Calendar cal = Calendar.getInstance();
        cal.setTime(mBooking.getStartDate());
        cal.add(Calendar.MINUTE, Math.round(mBooking.getHours() * MINUTES_PER_HOUR)); // adds booking duration
        final Date endDate = cal.getTime();

        //make sure this date is in the timezone of the booking location. This will be shown to the user
        final String end = DateTimeUtils.formatDate(endDate, SUBTITLE_DATE_FORMAT,
                mBooking.getBookingTimezone());

        final String duration = TextUtils.formatDecimal(mBooking.getHours(), DURATION_FORMAT);

        return context.getString(
                R.string.booking_card_row_time_and_duration,
                start,
                end,
                duration
        );
    }

    public boolean isIndicatorVisible()
    {
        final long now = System.currentTimeMillis();
        final long start = mBooking.getStartDate().getTime();
        final long delta = now - start;
        //TODO: Come up with specs an implement this properly
        return delta < 0 && delta > MIN_NEGATIVE_DELTA_MS_FOR_ON_MY_WAY;
    }

    public Booking getBooking()
    {
        return mBooking;
    }

    public static class List extends ArrayList<BookingCardRowViewModel>
    {
        public static BookingCardRowViewModel.List from(@NonNull final Collection<Booking> bookings)
        {
            final List list = new List();
            for (Booking eachBooking : bookings)
            {
                list.add(BookingCardRowViewModel.from(eachBooking));
            }
            return list;
        }
    }
}
