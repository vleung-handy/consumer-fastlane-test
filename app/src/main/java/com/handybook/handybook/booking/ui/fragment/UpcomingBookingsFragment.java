package com.handybook.handybook.booking.ui.fragment;

import android.animation.LayoutTransition;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.handybook.handybook.R;
import com.handybook.handybook.account.ui.EditPlanFragment;
import com.handybook.handybook.booking.BookingEvent;
import com.handybook.handybook.booking.model.Booking;
import com.handybook.handybook.booking.model.RecurringBooking;
import com.handybook.handybook.booking.model.Service;
import com.handybook.handybook.booking.ui.view.ServiceCategoriesOverlayFragment;
import com.handybook.handybook.core.constant.ActivityResult;
import com.handybook.handybook.library.ui.fragment.InjectedFragment;
import com.handybook.handybook.library.util.FragmentUtils;
import com.handybook.handybook.library.util.UiUtils;
import com.handybook.handybook.logger.handylogger.LogEvent;
import com.handybook.handybook.logger.handylogger.model.booking.UpcomingBookingsLog;
import com.handybook.handybook.logger.handylogger.model.user.ShareModalLog;
import com.handybook.handybook.referral.ui.ReferralFragment;
import com.handybook.handybook.core.ui.activity.MenuDrawerActivity;
import com.handybook.handybook.core.ui.view.BookingListItem;
import com.handybook.handybook.core.ui.view.ExpandableCleaningPlan;
import com.handybook.handybook.core.ui.view.NoBookingsView;
import com.handybook.handybook.core.ui.view.ShareBannerView;
import com.squareup.otto.Subscribe;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * the new way of doing things. We will only show the upcoming bookings here, and show active
 * bookings.
 */
public class UpcomingBookingsFragment extends InjectedFragment implements SwipeRefreshLayout.OnRefreshListener
{
    public static final String mOverlayFragmentTag = ServiceCategoriesOverlayFragment.class.getSimpleName();
    private static final String TAG = UpcomingBookingsFragment.class.getName();

    @Bind(R.id.add_booking_button)
    FloatingActionButton mAddBookingButton;

    @Bind(R.id.toolbar)
    Toolbar mToolbar;

    @Bind(R.id.swipe_refresh_layout)
    SwipeRefreshLayout mSwipeRefreshLayout;

    @Bind(R.id.scroll_view)
    ScrollView mScrollView;

    @Bind(R.id.main_container)
    LinearLayout mMainContainer;

    @Bind(R.id.bookings_container)
    LinearLayout mBookingsContainer;

    @Bind(R.id.parent_bookings_container)
    LinearLayout mParentBookingsContainer;

    @Bind(R.id.active_booking_container)
    LinearLayout mActiveBookingContainer;

    @Bind(R.id.no_booking_view)
    NoBookingsView mNoBookingsView;

    @Bind(R.id.text_upcoming_bookings)
    TextView mTextUpcomingBookings;

    @Bind(R.id.expanable_cleaning_plan)
    ExpandableCleaningPlan mExpandableCleaningPlan;

    @Bind(R.id.upcoming_bookings_padding_view)
    View mPaddingView;

    @Bind(R.id.fetch_error_view)
    ViewGroup mFetchErrorView;


    private List<Booking> mBookings;
    private List<RecurringBooking> mRecurringBookings;
    private int mActivePlanCount;
    private List<Service> mServices;
    private ShareBannerView mShareBannerView;
    private boolean mServiceRequestCompleted = false;
    private boolean mBookingsRequestCompleted = false;

    // This listener is added to FragmentManager in onStart() and removed in onStop()
    private FragmentManager.OnBackStackChangedListener mOnBackStackChangedListener =
            new FragmentManager.OnBackStackChangedListener()
            {
                @Override
                public void onBackStackChanged()
                {
                    int backStackEntryCount = getActivity().getSupportFragmentManager()
                                                           .getBackStackEntryCount();
                    // The button needs to be hidden when the option overlay buttons appear
                    // TODO: This is hacky. It will fail if the UpcomingBookingsFragment is not the first fragment in the stack
                    if (backStackEntryCount == 0)
                    {
                        mAddBookingButton.show();
                    }
                    else
                    {
                        mAddBookingButton.hide();
                    }
                }
            };

    public UpcomingBookingsFragment()
    {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of this fragment using the provided
     * parameters.
     *
     * @return A new instance of fragment BookingsFragmentTabbed.
     */
    public static UpcomingBookingsFragment newInstance()
    {
        return new UpcomingBookingsFragment();
    }

    @Override
    public void onActivityResult(final int requestCode, final int resultCode, final Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == ActivityResult.BOOKING_UPDATED
                || resultCode == ActivityResult.BOOKING_CANCELED)
        {
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
    )
    {
        final View view = inflater.inflate(R.layout.fragment_upcoming_bookings, container, false);
        ButterKnife.bind(this, view);

        setupToolbar(mToolbar, getString(R.string.my_bookings));
        if (mConfigurationManager.getPersistentConfiguration().isBottomNavEnabled())
        {
            mToolbar.setNavigationIcon(null);
        }
        else if (getActivity() instanceof MenuDrawerActivity)
        {
            ((MenuDrawerActivity) getActivity()).setupHamburgerMenu(mToolbar);
        }

        mSwipeRefreshLayout.setOnRefreshListener(this);
        mSwipeRefreshLayout.setColorSchemeResources(
                R.color.handy_service_handyman,
                R.color.handy_service_electrician,
                R.color.handy_service_cleaner,
                R.color.handy_service_painter,
                R.color.handy_service_plumber
        );

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
        {
            mMainContainer.getLayoutTransition().enableTransitionType(LayoutTransition.CHANGING);
        }

        mShareBannerView = new ShareBannerView(getActivity());
        mShareBannerView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(final View v)
            {
                bus.post(new LogEvent.AddLogEvent(new UpcomingBookingsLog.UpcomingBookingsShareBannerTappedLog()));
                Fragment fragment = ReferralFragment.newInstance(ShareModalLog.SRC_UPCOMING_BOOKINGS);
                FragmentUtils.switchToFragment(UpcomingBookingsFragment.this, fragment, true);
            }
        });

        return view;
    }

    @Subscribe
    public void onReceiveServicesSuccess(final BookingEvent.ReceiveServicesSuccess event)
    {
        mServiceRequestCompleted = true;
        mServices = event.getServices();
        if (mServices != null)
        {
            if (ViewCompat.isAttachedToWindow(mAddBookingButton))
            {
                UiUtils.revealView(mAddBookingButton);
            }
            else
            {
                mAddBookingButton.setVisibility(View.VISIBLE);
            }
        }

        setupBookingsView();
    }

    @OnClick(R.id.add_booking_button)
    public void onServicesButtonClicked()
    {
        final FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        if (fragmentManager.findFragmentByTag(mOverlayFragmentTag) == null)
        {
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

    @OnClick(R.id.bookings_share_button)
    public void onShareButtonClicked()
    {
        bus.post(new LogEvent.AddLogEvent(new UpcomingBookingsLog.UpcomingBookingsShareMenuPressedLog()));
        Fragment fragment = ReferralFragment.newInstance(ShareModalLog.SRC_UPCOMING_BOOKINGS);
        FragmentUtils.switchToFragment(UpcomingBookingsFragment.this, fragment, true);
    }

    @OnClick(R.id.try_again_button)
    public void reFetch()
    {
        loadBookings();
    }


    protected void loadBookings()
    {
        mBookingsRequestCompleted = false;
        mSwipeRefreshLayout.setRefreshing(true);
        bus.post(new BookingEvent.RequestBookings(Booking.List.VALUE_ONLY_BOOKINGS_UPCOMING));
    }

    private void bindBookingsToList()
    {
        if (mBookings == null || mBookings.isEmpty()) {return;}

        //active bookings, if any, are always at the top of the list.
        int bookingsIndex = -1;
        for (int i = 0; i < mBookings.size(); i++)
        {
            Booking booking = mBookings.get(i);
            if (booking.getActiveBookingLocationStatus() != null && booking.getActiveBookingLocationStatus()
                                                                           .isMapEnabled())
            {

                if (getChildFragmentManager().findFragmentByTag(booking.getId()) == null)
                {
                    ActiveBookingFragment frag = ActiveBookingFragment.newInstance(booking);
                    frag.setParentScrollView(mScrollView);

                    mActiveBookingContainer.removeAllViews();
                    //important here to use booking id as TAG, so that there aren't conflicts with multiple active bookings.
                    getChildFragmentManager().beginTransaction()
                                             .add(
                                                     R.id.active_booking_container,
                                                     frag,
                                                     booking.getId()
                                             )
                                             .commit();

                    //must executePendingTransactions now, otherwise the insertion of the share banner into this
                    //container will have an unpredictable location (due to commit being async)
                    getChildFragmentManager().executePendingTransactions();
                }

                mActiveBookingContainer.setVisibility(View.VISIBLE);
            }
            else
            {
                //at this index, we no longer have active bookings.
                bookingsIndex = i;
                break;
            }
        }

        if (bookingsIndex > -1)
        {
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
    private void setupForUpcomingBookings(int startingIndex)
    {
        mBookingsContainer.removeAllViews();
        for (int i = startingIndex; i < mBookings.size(); i++)
        {
            final Booking booking = mBookings.get(i);
            mBookingsContainer.addView(new BookingListItem(
                    getActivity(),
                    new View.OnClickListener()
                    {
                        @Override
                        public void onClick(final View v)
                        {
                            bus.post(new LogEvent.AddLogEvent(new UpcomingBookingsLog.BookingDetailsTappedLog(
                                    booking.getId())));
                            Fragment fragment = BookingDetailFragment.newInstance(booking, false);
                            FragmentUtils.switchToFragment(
                                    UpcomingBookingsFragment.this, fragment, true);
                            enableRefresh();
                        }
                    },
                    booking,
                    mConfigurationManager.getPersistentConfiguration()
                                         .isBookingHoursClarificationExperimentEnabled()
            ));

            //add divider
            mBookingsContainer.addView(getActivity().getLayoutInflater()
                                                    .inflate(
                                                            R.layout.layout_divider,
                                                            mBookingsContainer,
                                                            false
                                                    ));
        }
    }

    private void enableRefresh()
    {
        mBookings = null;
        mRecurringBookings = null;
    }

    /**
     * There are quite a bit of views that show/hide. This method manages all those states
     */
    private void updateVisibilityState()
    {
        if (mBookings == null || mBookings.isEmpty())
        {
            //no bookings
            mParentBookingsContainer.setVisibility(View.GONE);

            if (mActivePlanCount > 0)
            {
                //if there are plans, show active plan, no booking view
                mNoBookingsView.bindForNoRecurringBookings(mRecurringBookings);
            }
            else
            {
                mNoBookingsView.bindForNoBookingsAtAll();
            }

            mNoBookingsView.setVisibility(View.VISIBLE);
            mPaddingView.setVisibility(View.GONE);
        }
        else
        {
            //there are bookings
            mParentBookingsContainer.setVisibility(View.VISIBLE);
            mNoBookingsView.setVisibility(View.GONE);
            mPaddingView.setVisibility(View.VISIBLE);

            //if the active bookings are showing, and there are inactive bookings, then show the "upcoming text"
            if (mActiveBookingContainer.getVisibility() == View.VISIBLE
                    && mBookingsContainer.getChildCount() > 0)
            {
                mTextUpcomingBookings.setVisibility(View.VISIBLE);
            }
            else
            {
                mTextUpcomingBookings.setVisibility(View.GONE);
            }

        }

        //active cleaning plans display independently of bookings
        if (mActivePlanCount > 0)
        {
            mExpandableCleaningPlan.setVisibility(View.VISIBLE);
        }
        else
        {
            mExpandableCleaningPlan.setVisibility(View.GONE);
        }

    }

    @Subscribe
    public void onReceiveServicesError(final BookingEvent.ReceiveServicesError error)
    {
        //we don't really care that the services errored out, we just won't display the
        //services icon.
        mServiceRequestCompleted = true;
    }

    @Subscribe
    public void onReceiveBookingsSuccess(@NonNull BookingEvent.ReceiveBookingsSuccess event)
    {
        mFetchErrorView.setVisibility(View.GONE);
        mBookingsRequestCompleted = true;
        Log.d(TAG, "onReceiveBookingsSuccess: " + event.getOnlyBookingsValue());
        mBookings = event.getBookingWrapper().getBookings();

        if (event.getBookingWrapper().getRecurringBookings() != null &&
                !event.getBookingWrapper().getRecurringBookings().isEmpty())
        {
            mRecurringBookings = event.getBookingWrapper().getRecurringBookings();
            mActivePlanCount = mRecurringBookings.size();
        }
        else
        {
            mActivePlanCount = 0;
        }

        setupBookingsView();
    }

    @Subscribe
    public void onReceiveBookingsError(@NonNull final BookingEvent.ReceiveBookingsError e)
    {
        mFetchErrorView.setVisibility(View.VISIBLE);
        mBookingsRequestCompleted = true;
        mSwipeRefreshLayout.setRefreshing(false);
        dataManagerErrorHandler.handleError(getActivity(), e.error);
    }

    /**
     * Only perform the setup if we got responses from both bookings and get services
     */
    private void setupBookingsView()
    {
        if (mBookingsRequestCompleted && mServiceRequestCompleted)
        {
            mSwipeRefreshLayout.setRefreshing(false);

            if (mActivePlanCount > 0)
            {
                mExpandableCleaningPlan.bind(
                        new View.OnClickListener()
                        {
                            @Override
                            public void onClick(final View v)
                            {
                                RecurringBooking rb = (RecurringBooking) v.getTag();
                                EditPlanFragment fragment = EditPlanFragment.newInstance(rb);
                                FragmentUtils.switchToFragment(
                                        UpcomingBookingsFragment.this, fragment, true);
                            }
                        },
                        mRecurringBookings,
                        getActiveCountString()
                );
            }

            bindBookingsToList();
            updateVisibilityState();

            if (mActiveBookingContainer.getVisibility() == View.VISIBLE)
            {
                //if we're showing the map, disable this pull to refresh thing.
                mSwipeRefreshLayout.setEnabled(false);
            }

            insertShareBannerView();
        }
    }

    /**
     * The logic for where to insert the share banner is as follows
     * <p/>
     * --If active booking is present --if there are no upcoming bookings then display right below
     * active booking --if there are 1 or more active bookings, then display below the first
     * upcoming booking --done --If there are no upcoming bookings (don't show anything) --done --If
     * there are multiple bookings, then place below the 2nd upcoming booking --done
     */
    private void insertShareBannerView()
    {
        ViewParent shareBannerViewParent = mShareBannerView.getParent();
        if (shareBannerViewParent != null && shareBannerViewParent instanceof ViewGroup)
        {
            ((ViewGroup) shareBannerViewParent).removeView(mShareBannerView);
            /*
            prevents java.lang.IllegalStateException:
            The specified child already has a parent.
            You must call removeView() on the child's parent first.
             */
        }
        if (mActiveBookingContainer.getVisibility() == View.VISIBLE)
        {
            if (mBookingsContainer.getChildCount() > 0)
            {
                //at index 2, because 0 has first booking, and 1 is the divider
                mBookingsContainer.addView(mShareBannerView, 2);
            }
            else if (mBookingsContainer.getChildCount() == 0)
            {
                mActiveBookingContainer.addView(mShareBannerView);
            }
        }
        else
        {
            //child count 4 means 2 bookings (2 booking view, 2 dividers)
            if (mBookingsContainer.getChildCount() >= 4)
            {
                //there are at least 2 upcoming bookings, insert after the 2nd booking
                mBookingsContainer.addView(mShareBannerView, 4);
            }
            else if (mBookingsContainer.getChildCount() >= 2)
            {
                //there is only one upcoming booking, so insert it there.
                //at index 2 will go below the first booking & divider
                mBookingsContainer.addView(mShareBannerView, 2);
            }
            else
            {
                //there are no active booking, and there are no upcoming bookings, so we don't do anything.
            }
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
            loadBookings();
        }
        else
        {
            mBookingsRequestCompleted = true;
        }

        if (mServices == null)
        {
            mServiceRequestCompleted = false;
            bus.post(new BookingEvent.RequestServices());
        }
        else
        {
            mServiceRequestCompleted = true;
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
        // Workaround to be able to display the SwipeRefreshLayout onStart
        // as in: http://stackoverflow.com/a/26860930/486332
        mSwipeRefreshLayout.setProgressViewOffset(
                false,
                0,
                (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                                                24, getResources().getDisplayMetrics()
                )
        );
        getActivity().getSupportFragmentManager()
                     .addOnBackStackChangedListener(mOnBackStackChangedListener);
    }

    @Override
    public void onStop()
    {
        super.onStop();
        getActivity().getSupportFragmentManager()
                     .removeOnBackStackChangedListener(mOnBackStackChangedListener);
    }

    @Override
    public void onRefresh()
    {
        loadBookings();
    }

}
