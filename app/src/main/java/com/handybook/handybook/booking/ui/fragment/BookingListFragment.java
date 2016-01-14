package com.handybook.handybook.booking.ui.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.handybook.handybook.R;
import com.handybook.handybook.booking.model.Booking;
import com.handybook.handybook.booking.ui.adapter.BookingCardAdapter;
import com.handybook.handybook.booking.viewmodel.BookingCardViewModel;
import com.handybook.handybook.event.HandyEvent;
import com.handybook.handybook.ui.fragment.InjectedFragment;
import com.handybook.handybook.ui.view.EmptiableRecyclerView;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

public class BookingListFragment extends InjectedFragment
        implements SwipeRefreshLayout.OnRefreshListener
{
    public static final String STATE_BOOKINGS = "state:bookings";
    public static final String STATE_BOOKINGS_RECEIVED = "state:bookings_received";
    private static final String KEY_LIST_TYPE = "key:booking_list_type";

    @Bind(R.id.fragment_booking_list_swipe_refresh_layout)
    SwipeRefreshLayout mSwipeRefreshLayout;
    @Bind(R.id.fragment_booking_list_booking_card_recycler_view)
    EmptiableRecyclerView mEmptiableRecyclerView;
    @Bind(R.id.card_empty)
    CardView mNoBookingsView;
    @Bind(R.id.card_empty_text)
    TextView mNoBookingsText;
    private int mListType;
    private BookingCardAdapter mBookingCardAdapter;
    private boolean mBookingsWereReceived;
    private LinearLayoutManager mLayoutManager;

    public static BookingListFragment newInstance(
            @BookingCardViewModel.List.ListType final int bookingListType
    )
    {
        BookingListFragment fragment = new BookingListFragment();
        Bundle arguments = new Bundle();
        arguments.putInt(KEY_LIST_TYPE, bookingListType);
        fragment.setArguments(arguments);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        mListType = getArguments().getInt(KEY_LIST_TYPE);
        BookingCardViewModel.List bookingCardViewModels = BookingCardViewModel.List.empty();
        if (savedInstanceState != null)
        {
            mBookingsWereReceived = savedInstanceState.getBoolean(STATE_BOOKINGS_RECEIVED, false);
            ArrayList<Booking> bookings = savedInstanceState.getParcelableArrayList(STATE_BOOKINGS);
            if (bookings != null && !bookings.isEmpty())
            {
                bookingCardViewModels = BookingCardViewModel.List.from(bookings, mListType);
            }
            else
            {
                mBookingsWereReceived = false;
            }
        }
        mBookingCardAdapter = new BookingCardAdapter(getActivity(), bookingCardViewModels);
    }

    @Override
    public final void onStart()
    {
        super.onStart();
        TypedValue typed_value = new TypedValue();
        // Workaround to be able to display the SwipeRefreshLayout onStart
        // as in: http://stackoverflow.com/a/26860930/486332
        getActivity().getTheme().resolveAttribute(android.support.v7.appcompat.R.attr.actionBarSize, typed_value, true);
        mSwipeRefreshLayout.setProgressViewOffset(false, 0, getResources().getDimensionPixelSize(typed_value.resourceId));
        if (!mBookingsWereReceived)
        {
            mNoBookingsView.setVisibility(View.GONE);
            loadBookings();
        }
    }

    @Override
    public final void onStop()
    {
        super.onStop();
        mSwipeRefreshLayout.setRefreshing(false);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View root = inflater.inflate(R.layout.fragment_booking_list, container, false);
        ButterKnife.bind(this, root);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mSwipeRefreshLayout.setColorSchemeResources(
                R.color.handy_service_handyman,
                R.color.handy_service_electrician,
                R.color.handy_service_cleaner,
                R.color.handy_service_painter,
                R.color.handy_service_plumber
        );
        mLayoutManager = new LinearLayoutManager(getActivity());
        mEmptiableRecyclerView.setLayoutManager(mLayoutManager);
        mEmptiableRecyclerView.setAdapter(mBookingCardAdapter);
        mEmptiableRecyclerView.setEmptyView(mNoBookingsView);
        // Only allow SwipeRefresh when Recycler scrolled all the way up
        mEmptiableRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener()
        {
            @Override
            public void onScrolled(final RecyclerView recyclerView, final int dx, final int dy)
            {
                super.onScrolled(recyclerView, dx, dy);
                if (!recyclerView.canScrollVertically(-1))
                {
                    mSwipeRefreshLayout.setEnabled(true);
                }
                else
                {
                    mSwipeRefreshLayout.setEnabled(false);
                }
            }
        });
        switch (mListType)
        {
            case BookingCardViewModel.List.TYPE_UPCOMING:
                mNoBookingsText.setText(R.string.no_booking_card_upcoming_text);
                break;
            case BookingCardViewModel.List.TYPE_PAST:
                mNoBookingsText.setText(R.string.no_booking_card_past_text);
                break;
        }
        return root;
    }

    @Override
    public final void onSaveInstanceState(final Bundle outState)
    {
        super.onSaveInstanceState(outState);
        if (mBookingsWereReceived)
        {
            outState.putParcelableArrayList(STATE_BOOKINGS,
                    mBookingCardAdapter.getBookingCardViewModels().getBookings());
            outState.putBoolean(STATE_BOOKINGS_RECEIVED, mBookingsWereReceived);
        }
    }

    @Subscribe
    public void onModelsReceived(@NonNull final HandyEvent.ResponseEvent.BookingCardViewModels e)
    {
        if (e.getPayload().getType() == mListType)
        {
            mSwipeRefreshLayout.setRefreshing(false);
            mBookingsWereReceived = true;
            mBookingCardAdapter.setBookingCardViewModels(e.getPayload());
            mBookingCardAdapter.notifyDataSetChanged();
        }
    }

    @Subscribe
    public void onModelsRequestError(@NonNull final HandyEvent.ResponseEvent.BookingCardViewModelsError e)
    {
        mSwipeRefreshLayout.setRefreshing(false);
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

    protected void loadBookings()
    {
        mSwipeRefreshLayout.setRefreshing(true);
        String onlyBookingValues = null;
        switch (mListType)
        {
            case BookingCardViewModel.List.TYPE_PAST:
                onlyBookingValues = Booking.List.VALUE_ONLY_BOOKINGS_PAST;
                break;
            case BookingCardViewModel.List.TYPE_UPCOMING:
                onlyBookingValues = Booking.List.VALUE_ONLY_BOOKINGS_UPCOMING;
                break;
        }
        if (onlyBookingValues == null)
        {
            // Load all of them
            bus.post(new HandyEvent.RequestEvent.BookingCardViewModelsEvent(
                    userManager.getCurrentUser()
            ));
        }
        else
        {
            bus.post(new HandyEvent.RequestEvent.BookingCardViewModelsEvent(
                    userManager.getCurrentUser(),
                    onlyBookingValues
            ));
        }
    }
}
