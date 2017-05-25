package com.handybook.handybook.booking.history;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.handybook.handybook.R;
import com.handybook.handybook.booking.model.Booking;
import com.handybook.handybook.booking.model.UserBookingsWrapper;
import com.handybook.handybook.booking.ui.fragment.BookingDetailFragment;
import com.handybook.handybook.core.constant.BundleKeys;
import com.handybook.handybook.core.data.DataManager;
import com.handybook.handybook.core.data.callback.FragmentSafeCallback;
import com.handybook.handybook.core.ui.view.SimpleDividerItemDecoration;
import com.handybook.handybook.library.ui.fragment.ProgressSpinnerFragment;
import com.handybook.handybook.library.ui.view.EmptiableRecyclerView;
import com.handybook.handybook.library.util.FragmentUtils;
import com.handybook.handybook.logger.handylogger.LogEvent;
import com.handybook.handybook.logger.handylogger.model.booking.PastBookingsLog;
import com.handybook.handybook.logger.handylogger.model.user.ShareModalLog;
import com.handybook.handybook.referral.ui.ReferralActivity;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 */
public class HistoryFragment extends ProgressSpinnerFragment
        implements SwipeRefreshLayout.OnRefreshListener {

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

    public static HistoryFragment newInstance() {
        return new HistoryFragment();
    }

    public static HistoryFragment newInstance(boolean shouldShowToolbar) {
        HistoryFragment fragment = new HistoryFragment();
        Bundle args = new Bundle();
        args.putBoolean(BundleKeys.SHOULD_SHOW_TOOLBAR, shouldShowToolbar);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(
            @NonNull final LayoutInflater inflater,
            @Nullable final ViewGroup container,
            @Nullable final Bundle savedInstanceState
    ) {
        ViewGroup view = (ViewGroup) super.onCreateView(inflater, container, savedInstanceState);
        view.addView(inflater.inflate(R.layout.fragment_history, container, false));
        ButterKnife.bind(this, view);

        /*
        toolbar will be hidden when the combined upcoming/past bookings tab is enabled
        not bothering to break this fragment into one without a toolbar
        because we might just remove the toolbar if that combined fragment
        is supposed to be permanently on
         */
        boolean shouldShowToolbar = getArguments() == null
                                    || getArguments().getBoolean(BundleKeys.SHOULD_SHOW_TOOLBAR);
        if (shouldShowToolbar) {
            setupToolbar(mToolbar, getString(R.string.history));
            mToolbar.setVisibility(View.VISIBLE);
        }
        else {
            mToolbar.setVisibility(View.GONE);
        }

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
        mEmptiableRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(final RecyclerView recyclerView, final int dx, final int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (!recyclerView.canScrollVertically(-1)) {
                    mSwipeRefreshLayout.setEnabled(true);
                }
                else {
                    mSwipeRefreshLayout.setEnabled(false);
                }
            }
        });
        mNoBookingsText.setText(R.string.no_booking_card_past_text);

        return view;
    }

    protected void loadBookings() {
        mBookingsRequestCompleted = false;

        //fixme test
        bookingManager.requestBookings(
                Booking.List.VALUE_ONLY_BOOKINGS_PAST,
                new FragmentSafeCallback<UserBookingsWrapper>(this) {
                    @Override
                    public void onCallbackSuccess(final UserBookingsWrapper response) {
                        onReceiveBookingsSuccess(response);
                    }

                    @Override
                    public void onCallbackError(final DataManager.DataManagerError error) {
                        onReceiveBookingsError(error);
                    }
                }
        );
    }

    private void onReceiveBookingsSuccess(@NonNull final UserBookingsWrapper response) {
        mBookingsRequestCompleted = true;
        mBookings = response.getBookings();
        setupBookingsView();
    }

    private void onReceiveBookingsError(@NonNull final DataManager.DataManagerError error) {
        mBookingsRequestCompleted = true;
        mSwipeRefreshLayout.setRefreshing(false);
        hideProgressSpinner();
        toast.setText("Error loading bookings, please try again.");
        toast.show();
        dataManagerErrorHandler.handleError(getActivity(), error);
    }

    private void bindBookingsToList() {
        if (mBookings != null) {
            mAdapter = new HistoryListAdapter(
                    mBookings,
                    mConfigurationManager.getPersistentConfiguration()
                                         .isBookingHoursClarificationExperimentEnabled(),
                    new View.OnClickListener() {
                        @Override
                        public void onClick(final View v) {
                            Booking booking = (Booking) v.getTag();
                            bus.post(new LogEvent.AddLogEvent(new PastBookingsLog.BookingDetailsTappedLog(
                                    booking.getId())));
                            Fragment fragment = BookingDetailFragment.newInstance(booking, false);
                            FragmentUtils.switchToFragment(getActivity(), fragment, true);
                        }
                    }
            );

            mEmptiableRecyclerView.setAdapter(mAdapter);
        }
    }

    /**
     * Only perform the setup if we got responses from both bookings and get services
     */
    private void setupBookingsView() {
        if (mBookingsRequestCompleted) {
            mSwipeRefreshLayout.setRefreshing(false);
            hideProgressSpinner();
            bindBookingsToList();
        }
    }

    @OnClick(R.id.bookings_share_button)
    public void onShareButtonClicked() {
        bus.post(new LogEvent.AddLogEvent(new PastBookingsLog.PastBookingsShareMenuPressedLog()));
        Intent intent = new Intent(getContext(), ReferralActivity.class);
        intent.putExtra(
                BundleKeys.REFERRAL_PAGE_SOURCE,
                ShareModalLog.NativeShareTappedLog.SRC_PAST_BOOKINGS
        );
        startActivity(intent);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (getUserVisibleHint()) {
            loadBookingDataAndViewsIfNecessary();
        }
    }

    /**
     * specifically this is needed because we only want this fragment to request for data
     * when its viewpager tab is selected, and thus visible, in
     * {@link com.handybook.handybook.booking.ui.fragment.UpcomingAndPastBookingsFragment}
     * because the booking history payload can be very large as there is currently no pagination,
     * and users will likely not want to see it often compared to upcoming bookings
     *
     * assuming that this method is only called when the fragment is in a legal state
     */
    @Override
    public void setUserVisibleHint(final boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisible() && isVisibleToUser) {
            loadBookingDataAndViewsIfNecessary();
        }
    }

    private void loadBookingDataAndViewsIfNecessary() {
        /*
        this logic was just moved from onResume()
        todo we shouldn't necessarily request bookings
        when the bookings list is empty because user really might not have any bookings
         */
        if (mBookings == null || mBookings.isEmpty()) {
            mNoBookingsView.setVisibility(View.GONE);
            showProgressSpinner();
            loadBookings();
        }
        else {
            mBookingsRequestCompleted = true;
        }

        setupBookingsView();
    }

    @Override
    public void onPause() {
        super.onPause();
        mSwipeRefreshLayout.setRefreshing(false);
        hideProgressSpinner();
    }

    @Override
    public final void onStart() {
        super.onStart();
        TypedValue typed_value = new TypedValue();
        // Workaround to be able to display the SwipeRefreshLayout onStart
        // as in: http://stackoverflow.com/a/26860930/486332
        getActivity().getTheme()
                     .resolveAttribute(
                             android.support.v7.appcompat.R.attr.actionBarSize,
                             typed_value,
                             true
                     );
        mSwipeRefreshLayout.setProgressViewOffset(
                false,
                0,
                getResources().getDimensionPixelSize(typed_value.resourceId)
        );
    }

    @Override
    public void onRefresh() {
        loadBookings();
    }
}
