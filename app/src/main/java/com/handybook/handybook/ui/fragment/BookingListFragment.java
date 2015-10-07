package com.handybook.handybook.ui.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.handybook.handybook.R;
import com.handybook.handybook.constant.ActivityResult;
import com.handybook.handybook.constant.BundleKeys;
import com.handybook.handybook.core.Booking;
import com.handybook.handybook.event.HandyEvent;
import com.handybook.handybook.ui.activity.BookingDetailActivity;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import butterknife.Bind;
import butterknife.ButterKnife;

public class BookingListFragment extends InjectedFragment implements OnRefreshListener
{

    public static final String KEY_BOOKINGS = "key:bookings";
    public static final String KEY_BOOKINGS_RECEIVED = "key:bookings_received";
    public static final int TYPE_UPCOMING = 1;
    public static final int TYPE_PAST = 2;
    private static final String KEY_BOOKING_LIST_TYPE = "key:booking_list_type";
    @Bind(R.id.fragment_booking_list_booking_recycler_view)
    RecyclerView vRecyclerView;
    @Bind(R.id.fragment_bookinng_list_swipe_refresh_layout)
    SwipeRefreshLayout vSwipeRefreshLayout;

    private int mBookingListType;
    private Context mContext;
    private BookingAdapter mBookingAdapter;
    private ArrayList<Booking> mBookings = new ArrayList<>();
    private boolean mBookingsWereReceived;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public BookingListFragment()
    {
    }

    public static BookingListFragment newInstance(final int bookingListType)
    {
        BookingListFragment fragment = new BookingListFragment();
        Bundle args = new Bundle();
        args.putInt(KEY_BOOKING_LIST_TYPE, bookingListType);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        mContext = getContext();
        if (savedInstanceState != null)
        {
            mBookingsWereReceived = savedInstanceState.getBoolean(KEY_BOOKINGS_RECEIVED, false);
            mBookings = savedInstanceState.getParcelableArrayList(KEY_BOOKINGS);
        }

        if (getArguments() != null)
        {
            mBookingListType = getArguments().getInt(KEY_BOOKING_LIST_TYPE);
            mBookingAdapter = new BookingAdapter();
        }
    }

    @Override
    public final void onStart()
    {
        super.onStart();
        if (!mBookingsWereReceived)
        {
            loadBookings();
        }
    }

    @Override
    public final void onStop()
    {
        super.onStop();
        //progressDialog.dismiss();
        vSwipeRefreshLayout.setRefreshing(false);
    }

    private void loadBookings()
    {
        bus.post(new HandyEvent.RequestBookingsForUser(userManager.getCurrentUser()));
        //progressDialog.show();
        vSwipeRefreshLayout.setRefreshing(true);
    }

    @Override
    public final void onActivityResult(
            final int requestCode,
            final int resultCode,
            final Intent data
    )
    {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == ActivityResult.RESULT_BOOKING_UPDATED
                || resultCode == ActivityResult.RESULT_BOOKING_CANCELED)
        {
            final boolean isCancel = resultCode == ActivityResult.RESULT_BOOKING_CANCELED;
            final Booking booking;
            if (isCancel)
            {
                booking = data.getParcelableExtra(BundleKeys.CANCELLED_BOOKING);
            } else
            {
                booking = data.getParcelableExtra(BundleKeys.UPDATED_BOOKING);
            }
            final String bookingId = booking.getId();
            //TODO: We are manually updating the booking in the list, should re-request from manager
            // which would have the updated booking in its cache
            for (int i = 0; i < mBookings.size(); i++)
            {
                final Booking upBooking = mBookings.get(i);
                if (upBooking.getId().equals(bookingId))
                {
                    if (isCancel)
                    {
                        mBookings.remove(i);
                    } else
                    {
                        mBookings.set(i, booking);
                    }
                    Collections.sort(mBookings, new Comparator<Booking>()
                    {
                        @Override
                        public int compare(Booking lhs, Booking rhs)
                        {
                            return lhs.getStartDate().compareTo(rhs.getStartDate());
                        }
                    });
                    initialize();
                    break;
                }
            }
            //And then we're just going and requesting everything again anyway.....
            //TODO: reloading all bookings here until there is a way to update recurring instances as well
            loadBookings();
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View root = inflater.inflate(R.layout.fragment_booking_list, container, false);
        ButterKnife.bind(this, root);
        vSwipeRefreshLayout.setOnRefreshListener(this);
        vRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        vRecyclerView.setAdapter(mBookingAdapter);
        return root;
    }

    @Override
    public final void onSaveInstanceState(final Bundle outState)
    {
        super.onSaveInstanceState(outState);
        if (mBookingsWereReceived)
        {
            outState.putParcelableArrayList(KEY_BOOKINGS, mBookings);
            outState.putBoolean(KEY_BOOKINGS_RECEIVED, mBookingsWereReceived);
        }
    }

    private void initialize()
    {
        mBookingAdapter.notifyDataSetChanged();
    }

    @Subscribe
    public void onReceiveBookingsSuccess(HandyEvent.ReceiveBookingsSuccess event)
    {
        mBookings = new ArrayList<>(event.bookings);
        mBookingsWereReceived = true;
        initialize();
        //progressDialog.dismiss();
        vSwipeRefreshLayout.setRefreshing(false);
    }

    @Subscribe
    public void onReceiveBookingsError(HandyEvent.ReceiveBookingsError event)
    {
        mBookingsWereReceived = false;
        initialize();
        //progressDialog.dismiss();
        vSwipeRefreshLayout.setRefreshing(false);
        dataManagerErrorHandler.handleError(getActivity(), event.error);
    }

    @Override
    public void onRefresh()
    {
        toast.setText("OnRefresh");
    }


    private class BookingCardHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener
    {
        private Booking mBooking;

        public BookingCardHolder(View itemView)
        {
            super(itemView);
            ButterKnife.bind(this,itemView);
            itemView.setOnClickListener(this);
        }

        public void bindBooking(final Booking booking)
        {
            mBooking = booking;
            //TODO:set all other properties
        }


        @Override
        public void onClick(View v)
        {
            final Intent intent = new Intent(mContext, BookingDetailActivity.class);
            intent.putExtra(BundleKeys.BOOKING, mBooking);
            startActivityForResult(intent, ActivityResult.RESULT_BOOKING_UPDATED);
        }
    }


    private class BookingAdapter extends RecyclerView.Adapter<BookingCardHolder>
    {

        @Override
        public BookingCardHolder onCreateViewHolder(ViewGroup parent, int viewType)
        {
            final View itemView = LayoutInflater.from(mContext)
                    .inflate(R.layout.layout_card_booking, null);
            return new BookingCardHolder(itemView);
        }

        @Override
        public void onBindViewHolder(final BookingCardHolder holder, int position)
        {
            Booking booking = mBookings.get(position);
            holder.bindBooking(booking);
        }

        @Override
        public int getItemCount()
        {
            return mBookings.size();
        }
    }

}
