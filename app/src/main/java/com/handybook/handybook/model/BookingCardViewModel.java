package com.handybook.handybook.model;

import android.support.annotation.NonNull;

import com.handybook.handybook.core.Booking;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;

public class BookingCardViewModel
{
    private ArrayList<Booking> mBookings;
    private String mTitle = "<DEFAULT_TITLE>";
    private String mSubtitle = "<DEFAULT_SUBTITLE>";

    public BookingCardViewModel(@NonNull final Collection<Booking> bookings)
    {
        mBookings = new ArrayList<>();
        final Iterator<Booking> bookingIterator = bookings.iterator();
        while (bookingIterator.hasNext())
        {
            mBookings.add(bookingIterator.next());
        }
        if (mBookings.isEmpty())
        {
            //TODO: We received an empty collection, what should we do?
        } else
        {
            Collections.sort(mBookings, Booking.COMPARATOR_DATE);
            final Booking firstBooking = mBookings.get(0);
            mTitle = firstBooking.getServiceName();
            mSubtitle = firstBooking.getRecurringInfo();
        }
    }

    public BookingCardViewModel(@NonNull final Booking booking)
    {
        this(Collections.singleton(booking));
    }

    public ArrayList<Booking> getBookings()
    {
        return mBookings;
    }

    public String getTitle()
    {
        return mTitle;
    }

    public String getSubtitle()
    {
        return mSubtitle;
    }

    public boolean isMultiCard()
    {
        return mBookings.size() > 1;
    }

    public static class List extends ArrayList<BookingCardViewModel>
    {

    }

}
