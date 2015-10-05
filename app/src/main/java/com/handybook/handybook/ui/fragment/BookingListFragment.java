package com.handybook.handybook.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.crashlytics.android.Crashlytics;
import com.handybook.handybook.R;

import static com.handybook.handybook.ui.fragment.BookingListFragment.BookingListType.*;

public class BookingListFragment extends InjectedFragment
{

    public enum BookingListType
    {
        PAST("booking_list_past"),
        UPCOMING("booking_list_upcoming");

        private String mValue;

        BookingListType(final String value)
        {
            mValue = value;
        }


        @Override
        public String toString()
        {
            return mValue;
        }

    }


    private static final String ARG_BOOKING_LIST_TYPE = "key_booking_list_type";

    private BookingListType mBookingListType;

    public static BookingListFragment newInstance(final BookingListType bookingListType)
    {
        BookingListFragment fragment = new BookingListFragment();
        Bundle args = new Bundle();
        args.putString(ARG_BOOKING_LIST_TYPE, bookingListType.toString());
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public BookingListFragment()
    {
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        if (getArguments() != null)
        {
            final String bookingListTypeString = getArguments().getString(ARG_BOOKING_LIST_TYPE);
            switch (valueOf(bookingListTypeString))
            {
                case PAST:
                    break;
                case UPCOMING:
                    break;
                default:
                    Crashlytics.log("BookingListFragment received unknown type");
            }
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View root = inflater.inflate(R.layout.fragment_booking_list, container, false);
        return root;
    }

}
