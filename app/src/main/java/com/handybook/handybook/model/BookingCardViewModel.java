package com.handybook.handybook.model;

import android.support.annotation.NonNull;

import com.handybook.handybook.core.Booking;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

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

        public static List from(@NonNull final Collection<Booking> bookings)
        {
            final HashMap<Long, BookingCardViewModel> recurringIdToBCVM = new HashMap<>();
            final List bookingCardViewModels = new List();
            final Iterator<Booking> bookingIterator = bookings.iterator();
            while (bookingIterator.hasNext())
            {
                Booking eachBooking = bookingIterator.next();
                // If it's part of recurring booking
                if (eachBooking.isRecurring())
                {
                    BookingCardViewModel bcvm = recurringIdToBCVM.get(
                            eachBooking.getRecurringId()
                    );
                    if (bcvm == null)
                    {
                        // We haven't seen a booking from this recurring series
                        bcvm = new BookingCardViewModel(eachBooking);
                        recurringIdToBCVM.put(eachBooking.getRecurringId(), bcvm);
                        bookingCardViewModels.add(bcvm);
                    } else {
                        // We have seen one from this series, add it to it's parent
                        bcvm.addBooking(eachBooking);
                    }
                }
                // If not part of recurring then just add to list
                else
                {
                    bookingCardViewModels.add(new BookingCardViewModel(eachBooking));
                }
            }
            return bookingCardViewModels;
        }
    }

    private void addBooking(@NonNull final Booking booking)
    {
        mBookings.add(booking);
    }

}
