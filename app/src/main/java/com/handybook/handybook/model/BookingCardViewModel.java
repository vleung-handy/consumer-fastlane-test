package com.handybook.handybook.model;

import android.os.Parcelable;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;

import com.handybook.handybook.core.Booking;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
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

    public BookingCardRowViewModel.List getBookingCardRowViewModels()
    {
        BookingCardRowViewModel.List bcrvms = BookingCardRowViewModel.List.from(getBookings());
        return bcrvms;
    }

    public static class List extends ArrayList<BookingCardViewModel>
    {
        public static final int TYPE_PAST = 100;
        public static final int TYPE_UPCOMING = 101;
        public static final int TYPE_MIXED = 102;

        private int mType;
        private ArrayList<Booking> mBookings;

        public ArrayList<Booking> getBookings()
        {

            return mBookings;
        }


        /**
         * Defines type of BookingCardViewModel.List
         * <p/>
         * IntDef used instead of ENUMs
         *
         * @see <a href="http://tools.android.com/tech-docs/support-annotations">Support Annotations</a>
         */
        @IntDef({TYPE_PAST, TYPE_UPCOMING, TYPE_MIXED})
        @Retention(RetentionPolicy.SOURCE)
        public @interface ListType
        {
        }

        public void setType(@ListType int type)
        {
            mType = type;
        }


        @ListType
        public int getType()
        {
            return mType;
        }


        public static List from(@NonNull final Collection<Booking> bookings, @ListType int type)
        {
            switch (type)
            {
                case TYPE_PAST:
                    final List pastList = from(bookings, true);
                    pastList.setType(type);
                    return pastList;
                case TYPE_UPCOMING:
                    final List upcomingList = from(bookings);
                    upcomingList.setType(type);
                    return upcomingList;
                default:
                    return new List();
            }
        }

        public static List from(
                @NonNull final Collection<Booking> bookings,
                final boolean doNotGroup
        )
        {
            if (doNotGroup)
            {
                final List bookingCardViewModels = new List();
                bookingCardViewModels.mBookings = new ArrayList<>();
                for (Booking eachBooking : bookings)
                {
                    // Add the BookingsCardViewModel
                    bookingCardViewModels.add(new BookingCardViewModel(eachBooking));
                    // Add it to the internal booking list
                    bookingCardViewModels.mBookings.add(eachBooking);
                }
                return bookingCardViewModels;
            } else
            {
                return from(bookings);
            }
        }

        public static List from(@NonNull final Collection<Booking> bookings)
        {
            final HashMap<Long, BookingCardViewModel> recurringIdToBCVM = new HashMap<>();
            final List bookingCardViewModels = new List();
            bookingCardViewModels.mBookings = new ArrayList<>();
            for (Booking eachBooking : bookings)
            {
                // Add it to the internal booking list
                bookingCardViewModels.mBookings.add(eachBooking);
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
                    } else
                    {
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
