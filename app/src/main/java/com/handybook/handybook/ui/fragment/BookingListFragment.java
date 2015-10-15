package com.handybook.handybook.ui.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.handybook.handybook.R;
import com.handybook.handybook.constant.ActivityResult;
import com.handybook.handybook.core.Booking;
import com.handybook.handybook.event.HandyEvent;
import com.handybook.handybook.model.BookingCardViewModel;
import com.handybook.handybook.ui.adapter.BookingCardAdapter;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

public class BookingListFragment extends InjectedFragment implements OnRefreshListener
{
    public static final String TAG = "BookingListFragment";
    public static final String KEY_BOOKINGS = "key:bookings";
    public static final String KEY_BOOKINGS_RECEIVED = "key:bookings_received";
    private static final String KEY_LIST_TYPE = "key:booking_list_type";
    private final BookingCardViewModel.List mBookingCardViewModels = new BookingCardViewModel.List();
    @Bind(R.id.fragment_booking_list_booking_card_recycler_view)
    RecyclerView vRecyclerView;
    @Bind(R.id.fragment_booking_list_swipe_refresh_layout)
    SwipeRefreshLayout vSwipeRefreshLayout;
    private int mListType;
    private Context mContext;
    private BookingCardAdapter mBookingCardAdapter;
    private ArrayList<Booking> mBookings = new ArrayList<>();
    private boolean mBookingsWereReceived;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public BookingListFragment()
    {
    }

    public static BookingListFragment newInstance(
            @BookingCardViewModel.List.ListType final int bookingListType
    )
    {
        BookingListFragment fragment = new BookingListFragment();
        Bundle args = new Bundle();
        args.putInt(KEY_LIST_TYPE, bookingListType);
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
            if (mBookings != null)
            {
                mBookingCardViewModels.addAll(BookingCardViewModel.List.from(mBookings));
            }
        }
        if (getArguments() != null)
        {
            mListType = getArguments().getInt(KEY_LIST_TYPE);
        }
        mBookingCardAdapter = new BookingCardAdapter(mContext, mBookingCardViewModels);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View root = inflater.inflate(R.layout.fragment_booking_list, container, false);
        ButterKnife.bind(this, root);
        vSwipeRefreshLayout.setOnRefreshListener(this);
        vRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        vRecyclerView.setAdapter(mBookingCardAdapter);
        return root;
    }

    @Override
    public final void onStart()
    {
        super.onStart();
        vSwipeRefreshLayout.setRefreshing(true);
        if (!mBookingsWereReceived)
        {
            loadBookings();
        }
    }

    @Override
    public final void onStop()
    {
        super.onStop();
        vSwipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public final void onSaveInstanceState(final Bundle outState)
    {
        super.onSaveInstanceState(outState);
        if (mBookingsWereReceived)
        {
            outState.putParcelableArrayList(KEY_BOOKINGS, mBookingCardViewModels.getBookings());
            outState.putBoolean(KEY_BOOKINGS_RECEIVED, mBookingsWereReceived);
        }
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
/*
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
                    Collections.sort(mBookings, Booking.COMPARATOR_DATE);
                    initialize();
                    break;
                }
            }
            //And then we're just going and requesting everything again anyway.....
            //TODO: reloading all bookings here until there is a way to update recurring instances as well
*/
            loadBookings();
        }
    }

    @Subscribe
    public void onReceiveBookingsSuccess(HandyEvent.ReceiveBookingsSuccess event)
    {
        vSwipeRefreshLayout.setRefreshing(false);
    }

    @Subscribe
    public void onModelsReceived(@NonNull final HandyEvent.Response.BookingCardViewModels e)
    {
        if (e.getPayload().getType() == mListType)
        {
            vSwipeRefreshLayout.setRefreshing(false);
            mBookingCardViewModels.clear();
            mBookingCardViewModels.addAll(e.getPayload());
            initialize();
        }
    }

    @Subscribe
    public void onModelsRequestError(@NonNull final HandyEvent.Response.BookingCardViewModelsError e)
    {
        vSwipeRefreshLayout.setRefreshing(false);
        mBookingsWereReceived = false;
        toast.setText("Error loading bookings, please try again.");
        toast.show();
        dataManagerErrorHandler.handleError(getActivity(), e.getPayload());
    }


    @Override
    public void onRefresh()
    {
        loadBookings();
    }

    private void loadBookings()
    {
        vSwipeRefreshLayout.setRefreshing(true);
        Log.d(TAG, "loadBookings :setRefreshing");
        bus.post(new HandyEvent.Request.Request.BookingCardViewModels(userManager.getCurrentUser()));
        Log.d(TAG, "loadBookings :bus.post");
    }

    private void initialize()
    {
        mBookingCardAdapter.notifyDataSetChanged();
    }


}
