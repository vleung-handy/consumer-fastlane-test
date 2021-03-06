package com.handybook.handybook.booking.ui.fragment;

import android.animation.LayoutTransition;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.handybook.handybook.R;
import com.handybook.handybook.booking.BookingEvent;
import com.handybook.handybook.booking.constant.ProviderAvailabilitySource;
import com.handybook.handybook.booking.model.Booking;
import com.handybook.handybook.booking.model.ProviderRequest;
import com.handybook.handybook.booking.model.RecurringBooking;
import com.handybook.handybook.booking.model.Service;
import com.handybook.handybook.booking.model.UserBookingsWrapper;
import com.handybook.handybook.booking.ui.activity.BookingDateActivity;
import com.handybook.handybook.booking.ui.view.ServiceCategoriesOverlayFragment;
import com.handybook.handybook.core.constant.ActivityResult;
import com.handybook.handybook.core.constant.BundleKeys;
import com.handybook.handybook.core.data.DataManager;
import com.handybook.handybook.core.data.callback.Retrofit2FragmentSafeCallback;
import com.handybook.handybook.core.ui.fragment.ReviewAppBannerFragment;
import com.handybook.handybook.core.ui.view.BookingListItem;
import com.handybook.handybook.core.ui.view.NoBookingsView;
import com.handybook.handybook.core.ui.view.ShareBannerView;
import com.handybook.handybook.library.ui.fragment.ProgressSpinnerFragment;
import com.handybook.handybook.library.util.FragmentUtils;
import com.handybook.handybook.logger.handylogger.LogEvent;
import com.handybook.handybook.logger.handylogger.constants.EventContext;
import com.handybook.handybook.logger.handylogger.model.booking.UpcomingBookingsLog;
import com.handybook.handybook.logger.handylogger.model.booking.ViewAvailabilityLog;
import com.handybook.handybook.logger.handylogger.model.user.ShareModalLog;
import com.handybook.handybook.referral.ui.ReferralV2Fragment;

import org.greenrobot.eventbus.Subscribe;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * the new way of doing things. We will only show the upcoming bookings here, and show active
 * bookings.
 */
public class UpcomingBookingsFragment extends ProgressSpinnerFragment
        implements SwipeRefreshLayout.OnRefreshListener {

    public static final String mOverlayFragmentTag
            = ServiceCategoriesOverlayFragment.class.getSimpleName();
    private static final String TAG = UpcomingBookingsFragment.class.getName();

    @BindView(R.id.add_booking_button)
    FloatingActionButton mAddBookingButton;

    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    @BindView(R.id.swipe_refresh_layout)
    SwipeRefreshLayout mSwipeRefreshLayout;

    @BindView(R.id.scroll_view)
    ScrollView mScrollView;

    @BindView(R.id.main_container)
    LinearLayout mMainContainer;

    @BindView(R.id.bookings_container)
    LinearLayout mBookingsContainer;

    @BindView(R.id.parent_bookings_container)
    LinearLayout mParentBookingsContainer;

    @BindView(R.id.active_booking_container)
    LinearLayout mActiveBookingContainer;

    @BindView(R.id.no_booking_view)
    NoBookingsView mNoBookingsView;

    @BindView(R.id.text_upcoming_bookings)
    TextView mTextUpcomingBookings;

    @BindView(R.id.upcoming_bookings_padding_view)
    View mPaddingView;

    @BindView(R.id.fetch_error_view)
    ViewGroup mFetchErrorView;

    @BindView(R.id.fragment_upcoming_bookings_review_app_banner_fragment_container)
    FrameLayout mReviewAppBannerFragmentContainer;

    private List<Booking> mBookings;
    private List<RecurringBooking> mRecurringBookings;
    private int mActivePlanCount;
    private List<Service> mServices;
    private ShareBannerView mShareBannerView;
    private boolean mServiceRequestCompleted = false;
    private boolean mBookingsRequestCompleted = false;

    // This listener is added to FragmentManager in onStart() and removed in onStop()
    private FragmentManager.OnBackStackChangedListener mOnBackStackChangedListener =
            new FragmentManager.OnBackStackChangedListener() {
                @Override
                public void onBackStackChanged() {
                    int backStackEntryCount = getActivity().getSupportFragmentManager()
                                                           .getBackStackEntryCount();
                    // The button needs to be hidden when the option overlay buttons appear
                    // TODO: This is hacky. It will fail if the UpcomingBookingsFragment is not the first fragment in the stack
                    if (backStackEntryCount == 0) {
                        mAddBookingButton.show();
                    }
                    else {
                        mAddBookingButton.hide();
                    }
                }
            };

    public UpcomingBookingsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of this fragment using the provided
     * parameters.
     *
     * @return A new instance of fragment BookingsFragmentTabbed.
     */
    public static UpcomingBookingsFragment newInstance() {
        return new UpcomingBookingsFragment();
    }

    public static UpcomingBookingsFragment newInstance(boolean shouldShowToolbar) {
        UpcomingBookingsFragment fragment = new UpcomingBookingsFragment();
        Bundle args = new Bundle();
        args.putBoolean(BundleKeys.SHOULD_SHOW_TOOLBAR, shouldShowToolbar);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == ActivityResult.BOOKING_UPDATED
            || resultCode == ActivityResult.BOOKING_CANCELED
            || resultCode == ActivityResult.RESCHEDULE_NEW_DATE) {
            //this happens before onResume, so we just have to null out the bookings and it'll reload onResume.
            enableRefresh();
        }
    }

    @Nullable
    @Override
    public View onCreateView(
            final LayoutInflater inflater,
            @Nullable final ViewGroup container,
            @Nullable final Bundle savedInstanceState
    ) {
        ViewGroup view = (ViewGroup) super.onCreateView(inflater, container, savedInstanceState);
        view.addView(inflater.inflate(R.layout.fragment_upcoming_bookings, container, false));
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
            setupToolbar(mToolbar, getString(R.string.my_bookings));
            mToolbar.setNavigationIcon(null);
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

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            mMainContainer.getLayoutTransition().enableTransitionType(LayoutTransition.CHANGING);
        }

        mShareBannerView = new ShareBannerView(getActivity());
        mShareBannerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                bus.post(new LogEvent.AddLogEvent(new UpcomingBookingsLog.UpcomingBookingsShareBannerTappedLog()));
                Fragment fragment
                        = ReferralV2Fragment.newInstance(ShareModalLog.SRC_UPCOMING_BOOKINGS);
                FragmentUtils.switchToFragment(getActivity(), fragment, true);
            }
        });

        initReviewAppBannerFragment();

        return view;
    }

    @Subscribe
    public void onReceiveServicesSuccess(final BookingEvent.ReceiveServicesSuccess event) {
        if (isBeingRemoved()) {
            return;
        }
        mServiceRequestCompleted = true;
        mServices = event.getServices();

        setupBookingsView();
    }

    /**
     * When using {@link FragmentUtils.switchToFragment()}, onPause may not be called before the
     * incoming fragment's onResume. Hence this fragment may not get a chance to unregister the bus
     * and receive the incoming events it is not supposed to listen for. This method tells us whether
     * or not this fragment is being removed, so we can ignore event posts.
     */
    private boolean isBeingRemoved() {
        return isRemoving();
    }

    @OnClick(R.id.add_booking_button)
    public void onServicesButtonClicked() {
        final FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        if (fragmentManager.findFragmentByTag(mOverlayFragmentTag) == null) {
            bus.post(new LogEvent.AddLogEvent(new UpcomingBookingsLog.AddBookingPressedLog()));
            fragmentManager.beginTransaction()
                           .setCustomAnimations(R.anim.fade_in, 0, 0, R.anim.fade_out)
                           .add(
                                   R.id.fragment_container,
                                   ServiceCategoriesOverlayFragment.newInstance(mServices),
                                   mOverlayFragmentTag
                           )
                           .addToBackStack(null)
                           .commit();
        }
    }

    @OnClick(R.id.try_again_button)
    public void reFetch() {
        showProgressSpinner();
        loadBookings();
    }

    protected void loadBookings() {
        mBookingsRequestCompleted = false;
        bookingManager.requestBookings(
                Booking.List.VALUE_ONLY_BOOKINGS_UPCOMING,
                new Retrofit2FragmentSafeCallback<UserBookingsWrapper>(this) {

                    @Override
                    public void onSuccess(final UserBookingsWrapper response) {
                        onReceiveBookingsSuccess(response);
                    }

                    @Override
                    public void onError(final DataManager.DataManagerError error) {
                        onReceiveBookingsError(error);
                    }
                }
        );
    }

    @VisibleForTesting
    protected void onReceiveBookingsSuccess(@NonNull final UserBookingsWrapper response) {
        mFetchErrorView.setVisibility(View.GONE);
        mBookingsRequestCompleted = true;
        mBookings = response.getBookings();

        if (response.getRecurringBookings() != null &&
            !response.getRecurringBookings().isEmpty()) {
            mRecurringBookings = response.getRecurringBookings();
            mActivePlanCount = mRecurringBookings.size();
        }
        else {
            mActivePlanCount = 0;
        }

        setupBookingsView();
    }

    private void onReceiveBookingsError(@NonNull DataManager.DataManagerError error) {
        mFetchErrorView.setVisibility(View.VISIBLE);
        mBookingsRequestCompleted = true;
        mSwipeRefreshLayout.setRefreshing(false);
        hideProgressSpinner();
        dataManagerErrorHandler.handleError(getActivity(), error);
    }

    private void bindBookingsToList() {
        if (mBookings == null || mBookings.isEmpty()) {return;}

        //active bookings, if any, are always at the top of the list.
        int bookingsIndex = -1;
        for (int i = 0; i < mBookings.size(); i++) {
            Booking booking = mBookings.get(i);
            if (booking.getActiveBookingLocationStatus() != null &&
                booking.getActiveBookingLocationStatus()
                       .isMapEnabled()) {

                if (getChildFragmentManager().findFragmentByTag(booking.getId()) == null) {
                    ActiveBookingFragment frag = ActiveBookingFragment.newInstance(booking);
                    frag.setParentScrollView(mScrollView);

                    mActiveBookingContainer.removeAllViews();
                    //important here to use booking id as TAG, so that there aren't conflicts with multiple active bookings.
                    getChildFragmentManager()
                            .beginTransaction()
                            .add(R.id.active_booking_container, frag, booking.getId())
                            .commit();

                    //must executePendingTransactions now, otherwise the insertion of the share banner into this
                    //container will have an unpredictable location (due to commit being async)
                    getChildFragmentManager().executePendingTransactions();
                }

                mActiveBookingContainer.setVisibility(View.VISIBLE);
            }
            else {
                //at this index, we no longer have active bookings.
                bookingsIndex = i;
                break;
            }
        }

        if (bookingsIndex > -1) {
            //there was at least one booking that is not active.
            setupForUpcomingBookings(bookingsIndex);
        }
    }

    /**
     * Takes the list of bookings, starting at index startingIndex, and puts the rest of the
     * bookings onto the upcoming bookings container
     *
     * @param startingIndex
     */
    private void setupForUpcomingBookings(int startingIndex) {
        mBookingsContainer.removeAllViews();
        for (int i = startingIndex; i < mBookings.size(); i++) {
            final Booking booking = mBookings.get(i);
            final ProviderRequest providerRequest = booking.getProviderRequest();
            mBookingsContainer.addView(new BookingListItem(
                    getActivity(),
                    new View.OnClickListener() {
                        @Override
                        public void onClick(final View v) {
                            bus.post(new LogEvent.AddLogEvent(new UpcomingBookingsLog.BookingDetailsTappedLog(
                                    booking.getId())));
                            Fragment fragment = BookingDetailFragment.newInstance(booking, false);
                            FragmentUtils.switchToFragment(
                                    getActivity(), fragment, true);
                            enableRefresh();
                        }
                    },
                    new View.OnClickListener() {
                        @Override
                        public void onClick(final View v) {
                            if (providerRequest != null && providerRequest.getProvider() != null) {
                                bus.post(new LogEvent.AddLogEvent(new ViewAvailabilityLog(
                                        EventContext.UPCOMING_BOOKINGS,
                                        booking.getId(),
                                        userManager.getCurrentUser().getId(),
                                        providerRequest.getProvider().getId(),
                                        getString(
                                                R.string.pro_is_busy_formatted,
                                                providerRequest.getProvider()
                                                               .getFirstNameAndLastInitial()
                                        )
                                )));

                                showProgressSpinner(true);
                                bookingManager.rescheduleBookingWithProAvailability(
                                        providerRequest.getProvider().getId(),
                                        booking,
                                        ProviderAvailabilitySource.PROVIDER_REQUEST_RESPONSE
                                );
                            }
                        }
                    },
                    booking,
                    mConfigurationManager.getPersistentConfiguration()
                                         .isBookingHoursClarificationExperimentEnabled()
            ));

            //add divider
            mBookingsContainer.addView(
                    getActivity().getLayoutInflater()
                                 .inflate(R.layout.layout_divider, mBookingsContainer, false));
        }
    }

    private void enableRefresh() {
        mBookings = null;
        mRecurringBookings = null;
    }

    /**
     * There are quite a bit of views that show/hide. This method manages all those states
     */
    private void updateVisibilityState() {
        if (mBookings == null || mBookings.isEmpty()) {
            //no bookings
            mParentBookingsContainer.setVisibility(View.GONE);

            if (mActivePlanCount > 0) {
                //if there are plans, show active plan, no booking view
                mNoBookingsView.bindForNoRecurringBookings(mRecurringBookings);
            }
            else {
                mNoBookingsView.bindForNoBookingsAtAll();
            }

            mNoBookingsView.setVisibility(View.VISIBLE);
            mPaddingView.setVisibility(View.GONE);
        }
        else {
            //there are bookings
            mParentBookingsContainer.setVisibility(View.VISIBLE);
            mNoBookingsView.setVisibility(View.GONE);
            mPaddingView.setVisibility(View.VISIBLE);

            //if the active bookings are showing, and there are inactive bookings, then show the "upcoming text"
            if (mActiveBookingContainer.getVisibility() == View.VISIBLE
                && mBookingsContainer.getChildCount() > 0) {
                mTextUpcomingBookings.setVisibility(View.VISIBLE);
            }
            else {
                mTextUpcomingBookings.setVisibility(View.GONE);
            }

        }
    }

    @Subscribe
    public void onReceiveServicesError(final BookingEvent.ReceiveServicesError error) {
        if (isBeingRemoved()) {
            return;
        }
        //we don't really care that the services errored out, we just won't display the
        //services icon.
        mServiceRequestCompleted = true;
    }

    /**
     * splitting this out from setupBookingsView() for clarity and easier testing
     */
    private void updateReviewAppBannerFragmentVisibility() {
        if (mBookingsRequestCompleted
            && mServiceRequestCompleted
            && mActiveBookingContainer.getVisibility() == View.GONE) {
            /*
            whether the fragment contents can be displayed, even if this is set,
            is still dependent on the review app manager
            */
            mReviewAppBannerFragmentContainer.setVisibility(View.VISIBLE);
        }
        else {
            /*
            don't want to show the review app banner while still loading data
            or when active bookings map is visible
             */
            mReviewAppBannerFragmentContainer.setVisibility(View.GONE);
        }
    }

    /**
     * Only perform the setup if we got responses from both bookings and get services
     */
    private void setupBookingsView() {
        if (mBookingsRequestCompleted && mServiceRequestCompleted) {
            mSwipeRefreshLayout.setRefreshing(false);
            hideProgressSpinner();

            bindBookingsToList();
            updateVisibilityState();

            if (mActiveBookingContainer.getVisibility() == View.VISIBLE) {
                //if we're showing the map, disable this pull to refresh thing.
                mSwipeRefreshLayout.setEnabled(false);
            }

            insertShareBannerView();
        }

        updateReviewAppBannerFragmentVisibility();
    }

    /**
     * init the review app banner fragment. it will handle everything related to the banner
     */
    private void initReviewAppBannerFragment() {
        ReviewAppBannerFragment reviewAppBannerFragment = ReviewAppBannerFragment.newInstance();
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.replace(
                R.id.fragment_upcoming_bookings_review_app_banner_fragment_container,
                reviewAppBannerFragment,
                ReviewAppBannerFragment.TAG
        );
        transaction.commit();
    }

    /**
     * The logic for where to insert the share banner is as follows
     * <p/>
     * --If active booking is present --if there are no upcoming bookings then display right below
     * active booking --if there are 1 or more active bookings, then display below the first
     * upcoming booking --done --If there are no upcoming bookings (don't show anything) --done --If
     * there are multiple bookings, then place below the 2nd upcoming booking --done
     */
    private void insertShareBannerView() {
        ViewParent shareBannerViewParent = mShareBannerView.getParent();
        if (shareBannerViewParent != null && shareBannerViewParent instanceof ViewGroup) {
            ((ViewGroup) shareBannerViewParent).removeView(mShareBannerView);
            /*
            prevents java.lang.IllegalStateException:
            The specified child already has a parent.
            You must call removeView() on the child's parent first.
             */
        }
        if (mActiveBookingContainer.getVisibility() == View.VISIBLE) {
            if (mBookingsContainer.getChildCount() > 0) {
                //at index 2, because 0 has first booking, and 1 is the divider
                mBookingsContainer.addView(mShareBannerView, 2);
            }
            else if (mBookingsContainer.getChildCount() == 0) {
                mActiveBookingContainer.addView(mShareBannerView);
            }
        }
        else {
            //child count 4 means 2 bookings (2 booking view, 2 dividers)
            if (mBookingsContainer.getChildCount() >= 4) {
                //there are at least 2 upcoming bookings, insert after the 2nd booking
                mBookingsContainer.addView(mShareBannerView, 4);
            }
            else if (mBookingsContainer.getChildCount() >= 2) {
                //there is only one upcoming booking, so insert it there.
                //at index 2 will go below the first booking & divider
                mBookingsContainer.addView(mShareBannerView, 2);
            }
            else {
                //there are no active booking, and there are no upcoming bookings, so we don't do anything.
            }
        }
    }

    private String getActiveCountString() {
        if (mActivePlanCount > 0) {
            return getActivity().getResources().getQuantityString(
                    R.plurals.active_cleaning_plan_formatted,
                    mActivePlanCount,
                    mActivePlanCount
            );
        }
        else {
            return null;
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        if (mBookings == null || mBookings.isEmpty()) {
            showProgressSpinner();
            loadBookings();
        }
        else {
            mBookingsRequestCompleted = true;
        }

        if (mServices == null) {
            mServiceRequestCompleted = false;
            bus.post(new BookingEvent.RequestServices());
        }
        else {
            mServiceRequestCompleted = true;
        }

        setupBookingsView();
    }

    @Override
    public void onPause() {
        super.onPause();
        mSwipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public final void onStart() {
        super.onStart();
        bus.register(this);
        // Workaround to be able to display the SwipeRefreshLayout onStart
        // as in: http://stackoverflow.com/a/26860930/486332
        mSwipeRefreshLayout.setProgressViewOffset(
                false,
                0,
                (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                                                24, getResources().getDisplayMetrics()
                )
        );
    }

    @Override
    public void onStop() {
        bus.unregister(this);
        super.onStop();
    }

    @Override
    public void onRefresh() {
        loadBookings();
    }

    @Subscribe
    public void onRescheduleWithAvailabilitySuccess(BookingEvent.RescheduleBookingWithProAvailabilitySuccess success) {
        hideProgressSpinner();

        final Intent intent = new Intent(getContext(), BookingDateActivity.class);
        intent.putExtra(BundleKeys.RESCHEDULE_BOOKING, success.getBooking());
        intent.putExtra(BundleKeys.RESCHEDULE_NOTICE, success.getNotice());
        intent.putExtra(
                BundleKeys.RESCHEDULE_TYPE,
                BookingDetailFragment.RescheduleType.FROM_PRO_BANNER
        );
        intent.putExtra(
                BundleKeys.PROVIDER_ID,
                success.getBooking().getProviderRequest().getProvider().getId()
        );
        intent.putExtra(BundleKeys.PRO_AVAILABILITY, success.getProAvailability());
        startActivityForResult(intent, ActivityResult.RESCHEDULE_NEW_DATE);
    }

    @Subscribe
    public void onRescheduleWithAvailabilityError(BookingEvent.RescheduleBookingWithProAvailabilityError error) {
        hideProgressSpinner();
        Toast.makeText(getContext(), R.string.reschedule_try_again, Toast.LENGTH_SHORT).show();
    }
}
