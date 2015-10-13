package com.handybook.handybook.model;

import android.content.Context;
import android.support.annotation.NonNull;

import com.handybook.handybook.R;
import com.handybook.handybook.core.Booking;
import com.handybook.handybook.util.TextUtils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;

public class BookingCardRowViewModel
{

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
        final String title = TextUtils.formatDate(mBooking.getStartDate(), "EEEE',' MMMM d");
        return title;
    }

    public String getSubtitle(@NonNull Context context)
    {
//        String timeText = TextUtils.formatDate(booking.getStartDate(), "h:mm aaa")
//                + " - "
//                + TextUtils.formatDecimal(booking.getHours(), "#.#")
//                + " "
//                + getString(R.string.hours);
        final String start = TextUtils.formatDate(mBooking.getStartDate(), "h:mm aaa");
        Calendar cal = Calendar.getInstance();
        cal.setTime(mBooking.getStartDate());
        cal.add(Calendar.MINUTE, Math.round(mBooking.getHours() * 60)); // adds booking duration
        final Date endDate = cal.getTime();
        final String end = TextUtils.formatDate(endDate, "h:mm aaa");
        final String duration = TextUtils.formatDecimal(mBooking.getHours(), "#.#");

        final String subtitle = context.getString(
                R.string.booking_card_row_time_and_duration,
                start,
                end,
                duration
        );
        return subtitle;
    }

    public boolean isIndicatorVisible()
    {
        final long now = System.currentTimeMillis();
        final long start = mBooking.getStartDate().getTime();
        final long delta = now - start;
        //TODO: Come up with specs an implement this properly
        if (delta < 0 && delta > MIN_NEGATIVE_DELTA_MS_FOR_ON_MY_WAY)
        {
            return true;
        }
        return false;
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
