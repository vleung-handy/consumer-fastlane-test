package com.handybook.handybook.booking.viewmodel;

import android.support.annotation.IntDef;
import android.support.annotation.NonNull;

import com.handybook.handybook.booking.model.Booking;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

public class BookingCardViewModel
{
    private static final Comparator<? super BookingCardViewModel> COMPARATOR_DATE_REVERSE
            = new Comparator<BookingCardViewModel>()
    {
        @Override
        public int compare(
                @NonNull final BookingCardViewModel lhs,
                @NonNull final BookingCardViewModel rhs
        )
        {
            if (lhs.getBookings().size() < 1)
            {
                return -1;
            }
            else if (rhs.getBookings().size() < 1)
            {
                return +1;
            }
            // rhs and lhs comparison reversed on purpose below
            return rhs.getBookings().get(0).getStartDate().compareTo(
                    lhs.getBookings().get(0).getStartDate());
        }
    };
    private ArrayList<Booking> mBookings;
    private String mTitle = "";
    private String mSubtitle = "";

    public BookingCardViewModel(@NonNull final Collection<Booking> bookings)
    {
        mBookings = new ArrayList<>();
        for (final Booking booking : bookings)
        {
            mBookings.add(booking);
        }
        if (mBookings.isEmpty())
        {
            //TODO: We received an empty collection, what should we do?
        }
        else
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

    private void addBooking(@NonNull final Booking booking)
    {
        mBookings.add(booking);
    }

    public static class List
    {
        public static final int TYPE_PAST = 100;
        public static final int TYPE_UPCOMING = 101;
        public static final int TYPE_MIXED = 102;

        private int mType;
        private ArrayList<BookingCardViewModel> mBookingCardViewModels = new ArrayList<>();

        public ArrayList<Booking> getBookings()
        {
            ArrayList<Booking> bookings = new ArrayList<>();
            for (BookingCardViewModel model : mBookingCardViewModels)
            {
                bookings.addAll(model.getBookings());
            }
            return bookings;
        }

        private List()
        {
        }

        public int size()
        {
            return mBookingCardViewModels.size();
        }

        public BookingCardViewModel get(final int position)
        {
            return mBookingCardViewModels.get(position);
        }

        /**
         * Defines type of BookingCardViewModel.List
         * <p/>
         * IntDef used instead of ENUMs
         *
         * @see <a href="http://tools.android.com/tech-docs/support-annotations">Support
         * Annotations</a>
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

        public static List empty()
        {
            return new List();
        }

        public static List from(@NonNull final Collection<Booking> bookings, @ListType int type)
        {
            switch (type)
            {
                case TYPE_PAST:
                    final List pastList = from(bookings, true);
                    // Reverse because we want them displayed in reverse
                    Collections.sort(pastList.mBookingCardViewModels,
                            BookingCardViewModel.COMPARATOR_DATE_REVERSE);
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

        private static List from(
                @NonNull final Collection<Booking> bookings,
                final boolean doNotGroup
        )
        {
            if (doNotGroup)
            {
                final List bookingCardViewModels = new List();
                for (Booking eachBooking : bookings)
                {
                    // Add the BookingsCardViewModel
                    bookingCardViewModels.mBookingCardViewModels
                            .add(new BookingCardViewModel(eachBooking));
                    // Add it to the internal booking list
                }
                return bookingCardViewModels;
            }
            else
            {
                return from(bookings);
            }
        }

        private static List from(@NonNull final Collection<Booking> bookings)
        {
            final HashMap<Long, BookingCardViewModel> recurringIdToBCVM = new HashMap<>();
            final List bookingCardViewModels = new List();
            for (Booking eachBooking : bookings)
            {
                // Add it to the internal booking list
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
                        bookingCardViewModels.mBookingCardViewModels.add(bcvm);
                    }
                    else
                    {
                        // We have seen one from this series, add it to its parent
                        bcvm.addBooking(eachBooking);
                    }
                }
                // If not part of recurring then just add to list
                else
                {
                    bookingCardViewModels.mBookingCardViewModels
                            .add(new BookingCardViewModel(eachBooking));
                }
            }
            return bookingCardViewModels;
        }
    }

}
