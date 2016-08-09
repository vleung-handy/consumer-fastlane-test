package com.handybook.handybook.module.bookings;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.handybook.handybook.R;
import com.handybook.handybook.booking.BookingEvent;
import com.handybook.handybook.booking.model.Booking;
import com.handybook.handybook.booking.model.RecurringBooking;
import com.handybook.handybook.booking.ui.activity.BookingDetailActivity;
import com.handybook.handybook.constant.ActivityResult;
import com.handybook.handybook.constant.BundleKeys;
import com.handybook.handybook.ui.fragment.InjectedFragment;
import com.handybook.handybook.ui.view.EmptiableRecyclerView;
import com.handybook.handybook.ui.view.SimpleDividerItemDecoration;
import com.squareup.otto.Subscribe;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 */
public class BookingListFragment extends InjectedFragment implements SwipeRefreshLayout.OnRefreshListener
{
    public static final String STATE_BOOKINGS = "state:bookings";
    public static final String STATE_RECURRING_BOOKINGS = "state:recurring_bookings";
    public static final String STATE_ACTIVE_PLAN_COUNT = "state:recurring_bookings";
    private static final String KEY_LIST_TYPE = "list_type";
    private static final String TAG = BookingListFragment.class.getName();

    @Bind(R.id.fragment_booking_list_swipe_refresh_layout)
    SwipeRefreshLayout mSwipeRefreshLayout;

    @Bind(R.id.fragment_booking_list_booking_card_recycler_view)
    EmptiableRecyclerView mEmptiableRecyclerView;

    @Bind(R.id.card_empty)
    View mNoBookingsView;

    @Bind(R.id.card_empty_text)
    TextView mNoBookingsText;

    private ListType mListType;
    private List<Booking> mBookings;
    private List<RecurringBooking> mRecurringBookings;
    private int mActivePlanCount;
    private SimpleBookingListAdapater mSimpleBookingListAdapater;
    private ActiveBookingListAdapter mActiveBookingListAdapter;

    private LinearLayoutManager mLayoutManager;

    //FIXME: JIA: make this come from the server.
    private boolean mShouldShowMapView = true;


    public static BookingListFragment newInstance(final ListType listType)
    {
        BookingListFragment fragment = new BookingListFragment();
        Bundle arguments = new Bundle();
        arguments.putString(KEY_LIST_TYPE, listType.getValue());
        fragment.setArguments(arguments);
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null)
        {
            mBookings = savedInstanceState.getParcelableArrayList(STATE_BOOKINGS);
            mRecurringBookings = (List<RecurringBooking>) savedInstanceState.getSerializable(STATE_RECURRING_BOOKINGS);
            mActivePlanCount = savedInstanceState.getInt(STATE_ACTIVE_PLAN_COUNT, 0);
        }

        if (getArguments() != null)
        {
            String value = getArguments().getString(KEY_LIST_TYPE);
            if (!TextUtils.isEmpty(value))
            {
                mListType = ListType.fromValue(value);
                Log.d(TAG, "onCreate: list type:" + mListType.getValue());
            }
        }
    }

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    )
    {
        final View view = inflater.inflate(R.layout.fragment_booking_list_v2, container, false);
        ButterKnife.bind(this, view);

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
        bindBookingsToList();
        // Only allow SwipeRefresh when Recycler scrolled all the way up

        return view;
    }

    @Override
    public final void onSaveInstanceState(final Bundle outState)
    {
        super.onSaveInstanceState(outState);
        if (mBookings != null && !mBookings.isEmpty())
        {
            //FIXME: JIA: test this to make sure it works.
            outState.putParcelableArrayList(STATE_BOOKINGS, (ArrayList<? extends Parcelable>) mBookings);
            outState.putSerializable(STATE_RECURRING_BOOKINGS, (Serializable) mRecurringBookings);
            outState.putInt(STATE_ACTIVE_PLAN_COUNT, mActivePlanCount);
        }
    }

    protected void loadBookings()
    {
        mSwipeRefreshLayout.setRefreshing(true);

        if (mListType == ListType.UPCOMING)
        {
            bus.post(new BookingEvent.RequestBookings(Booking.List.VALUE_ONLY_BOOKINGS_UPCOMING));
        }
        else
        {
            bus.post(new BookingEvent.RequestBookings(Booking.List.VALUE_ONLY_BOOKINGS_PAST));
        }
    }

    private void bindBookingsToList()
    {
        if (mBookings != null)
        {
            if (mListType == ListType.UPCOMING)
            {
                mActiveBookingListAdapter = new ActiveBookingListAdapter(
                        getChildFragmentManager(),
                        mBookings,
                        mRecurringBookings,
                        getActiveCountString(),
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
                mEmptiableRecyclerView.setAdapter(mActiveBookingListAdapter);
            }
            else
            {

                mSimpleBookingListAdapater = new SimpleBookingListAdapater(
                        getChildFragmentManager(),
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

                mEmptiableRecyclerView.setAdapter(mSimpleBookingListAdapater);

            }
        }
    }


    @Subscribe
    public void onReceiveBookingsSuccess(@NonNull BookingEvent.ReceiveBookingsSuccess event)
    {

        ListType type = ListType.fromValue(event.getOnlyBookingsValue());
        if (type == null || !mListType.getValue().equals(event.getOnlyBookingsValue()))
        {
            return;
        }

        Log.d(TAG, "onReceiveBookingsSuccess: " + event.getOnlyBookingsValue());
        if (event.getBookingWrapper().getRecurringBookings() != null &&
                !event.getBookingWrapper().getRecurringBookings().isEmpty())
        {
            mActivePlanCount = event.getBookingWrapper().getRecurringBookings().size();
            mRecurringBookings = event.getBookingWrapper().getRecurringBookings();
        }
        else
        {
            mActivePlanCount = 0;
        }

        mBookings = event.getBookingWrapper().getBookings();
        mSwipeRefreshLayout.setRefreshing(false);

        bindBookingsToList();

//        FIXME: JIA: only disable this when the mapview is turned on.
        if (mShouldShowMapView)
        {
            //if we're showing the map, disable this pull to refresh thing.
            mSwipeRefreshLayout.setEnabled(false);
        }
        else
        {
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
        }
    }


    private String getActiveCountString()
    {
        if (mActivePlanCount > 0)
        {
            return getActivity().getResources().getQuantityString(
                    R.plurals.active_cleaning_plan_formatted,
                    mActivePlanCount,
                    mActivePlanCount
            );
        }
        else
        {
            return null;
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


    @Subscribe
    public void onReceiveBookingsError(@NonNull final BookingEvent.ReceiveBookingsError e)
    {
        mSwipeRefreshLayout.setRefreshing(false);
        toast.setText("Error loading bookings, please try again.");
        toast.show();
        dataManagerErrorHandler.handleError(getActivity(), e.error);
    }


}
