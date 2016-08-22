package com.handybook.handybook.module.bookings;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.handybook.handybook.R;
import com.handybook.handybook.booking.BookingEvent;
import com.handybook.handybook.booking.model.Booking;
import com.handybook.handybook.booking.ui.activity.BookingDetailActivity;
import com.handybook.handybook.constant.ActivityResult;
import com.handybook.handybook.constant.BundleKeys;
import com.handybook.handybook.ui.activity.MenuDrawerActivity;
import com.handybook.handybook.ui.fragment.InjectedFragment;
import com.handybook.handybook.ui.view.EmptiableRecyclerView;
import com.handybook.handybook.ui.view.SimpleDividerItemDecoration;
import com.squareup.otto.Subscribe;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 */
public class HistoryFragment extends InjectedFragment implements SwipeRefreshLayout.OnRefreshListener
{
    private static final String TAG = HistoryFragment.class.getName();

    @Bind(R.id.history_swipe_refresh_layout)
    SwipeRefreshLayout mSwipeRefreshLayout;

    @Bind(R.id.history_recycler_view)
    EmptiableRecyclerView mEmptiableRecyclerView;

    @Bind(R.id.card_empty)
    View mNoBookingsView;

    @Bind(R.id.card_empty_text)
    TextView mNoBookingsText;

    @Bind(R.id.toolbar)
    Toolbar mToolbar;

    private List<Booking> mBookings;
    private LinearLayoutManager mLayoutManager;
    private HistoryListAdapter mAdapter;
    private boolean mBookingsRequestCompleted = false;

    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable final Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_history, container, false);
        ButterKnife.bind(this, view);

        setupToolbar(mToolbar, getString(R.string.history));
        ((MenuDrawerActivity) getActivity()).setupHamburgerMenu(mToolbar);

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
        mEmptiableRecyclerView.setEmptyView(mNoBookingsView);
        mEmptiableRecyclerView.addItemDecoration(new SimpleDividerItemDecoration(getActivity()));

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
        mNoBookingsText.setText(R.string.no_booking_card_past_text);

        return view;
    }

    protected void loadBookings()
    {
        mBookingsRequestCompleted = false;
        mSwipeRefreshLayout.setRefreshing(true);
        bus.post(new BookingEvent.RequestBookings(Booking.List.VALUE_ONLY_BOOKINGS_PAST));
    }

    private void bindBookingsToList()
    {
        if (mBookings != null)
        {
            mAdapter = new HistoryListAdapter(
                    mBookings,
                    new View.OnClickListener()
                    {
                        @Override
                        public void onClick(final View v)
                        {
                            final Intent intent = new Intent(getActivity(), BookingDetailActivity.class);
                            Booking booking = (Booking) v.getTag();
                            intent.putExtra(BundleKeys.BOOKING, booking);
                            getActivity().startActivityForResult(intent, ActivityResult.BOOKING_UPDATED);
                        }
                    });

            mEmptiableRecyclerView.setAdapter(mAdapter);
        }
    }

    @Subscribe
    public void onReceiveBookingsSuccess(@NonNull BookingEvent.ReceiveBookingsSuccess event)
    {
        mBookingsRequestCompleted = true;
        Log.d(TAG, "onReceiveBookingsSuccess: " + event.getOnlyBookingsValue());
        mBookings = event.getBookingWrapper().getBookings();
        setupBookingsView();
    }

    @Subscribe
    public void onReceiveBookingsError(@NonNull final BookingEvent.ReceiveBookingsError e)
    {
        mBookingsRequestCompleted = true;
        mSwipeRefreshLayout.setRefreshing(false);
        toast.setText("Error loading bookings, please try again.");
        toast.show();
        dataManagerErrorHandler.handleError(getActivity(), e.error);
    }

    /**
     * Only perform the setup if we got responses from both bookings and get services
     */
    private void setupBookingsView()
    {
        if (mBookingsRequestCompleted)
        {
            mSwipeRefreshLayout.setRefreshing(false);
            bindBookingsToList();
        }
    }


    @Override
    public void onResume()
    {
        super.onResume();

        if (mBookings == null || mBookings.isEmpty())
        {
            mNoBookingsView.setVisibility(View.GONE);
            loadBookings();
        }
        else
        {
            mBookingsRequestCompleted = true;
        }

        setupBookingsView();
    }

    @Override
    public void onPause()
    {
        super.onPause();
        mSwipeRefreshLayout.setRefreshing(false);
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
    }

    @Override
    public void onRefresh()
    {
        loadBookings();
    }
}
