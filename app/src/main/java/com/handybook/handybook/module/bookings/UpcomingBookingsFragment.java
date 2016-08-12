package com.handybook.handybook.module.bookings;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.handybook.handybook.R;
import com.handybook.handybook.booking.BookingEvent;
import com.handybook.handybook.booking.bookingedit.ui.activity.BookingEditFrequencyActivity;
import com.handybook.handybook.booking.model.Booking;
import com.handybook.handybook.booking.model.Service;
import com.handybook.handybook.booking.model.UserRecurringBooking;
import com.handybook.handybook.booking.ui.activity.BookingDetailActivity;
import com.handybook.handybook.booking.ui.view.ServiceCategoriesOverlayFragment;
import com.handybook.handybook.constant.ActivityResult;
import com.handybook.handybook.constant.BundleKeys;
import com.handybook.handybook.logger.mixpanel.MixpanelEvent;
import com.handybook.handybook.ui.activity.MenuDrawerActivity;
import com.handybook.handybook.ui.fragment.InjectedFragment;
import com.handybook.handybook.ui.view.BookingListItem;
import com.handybook.handybook.ui.view.ExpandableCleaningPlan;
import com.handybook.handybook.util.UiUtils;
import com.squareup.otto.Subscribe;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * the new way of doing things. We will only show the upcoming bookings here, and show
 * active bookings.
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

    @Bind(R.id.active_booking_container)
    LinearLayout mActiveBookingContainer;

    @Bind(R.id.card_empty)
    View mNoBookingsView;

    @Bind(R.id.card_empty_text)
    TextView mNoBookingsText;

    @Bind(R.id.expanable_cleaning_plan)
    ExpandableCleaningPlan mExpandableCleaningPlan;

    private List<Booking> mBookings;
    private List<UserRecurringBooking> mRecurringBookings;
    private int mActivePlanCount;
    private List<Service> mServices;

    private boolean mServiceRequestCompleted = false;
    private boolean mBookingsRequestCompleted = false;


    //FIXME: JIA: make this come from the server.
    private boolean mShouldShowMapView = true;

    public UpcomingBookingsFragment()
    {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
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
            mBookings = null;
            mRecurringBookings = null;
        }
    }

    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable final Bundle savedInstanceState)
    {
        final View view = inflater.inflate(R.layout.fragment_upcoming_bookings, container, false);
        ButterKnife.bind(this, view);

        setupToolbar(mToolbar, getString(R.string.my_bookings));

        if (getActivity() instanceof MenuDrawerActivity)
        {
            ((MenuDrawerActivity) getActivity()).setupHamburgerMenu(mToolbar);
        }

        getActivity().getSupportFragmentManager()
                .addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener()
                {
                    @Override
                    public void onBackStackChanged()
                    {
                        int backStackEntryCount = getActivity().getSupportFragmentManager()
                                .getBackStackEntryCount();
                        if (backStackEntryCount == 0)
                        {
                            mAddBookingButton.show();
                        }
                        else
                        {
                            mAddBookingButton.hide();
                        }
                    }
                });

        mSwipeRefreshLayout.setOnRefreshListener(this);
        mSwipeRefreshLayout.setColorSchemeResources(
                R.color.handy_service_handyman,
                R.color.handy_service_electrician,
                R.color.handy_service_cleaner,
                R.color.handy_service_painter,
                R.color.handy_service_plumber
        );

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
        bus.post(new MixpanelEvent.TrackAddBookingFabClicked());

        final FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        if (fragmentManager.findFragmentByTag(mOverlayFragmentTag) == null)
        {
            fragmentManager.beginTransaction()
                    .setCustomAnimations(R.anim.fade_in, 0, 0, R.anim.fade_out)
                    .add(R.id.fragment_container,
                            ServiceCategoriesOverlayFragment.newInstance(mServices),
                            mOverlayFragmentTag)
                    .addToBackStack(null)
                    .commit();
        }
    }


    protected void loadBookings()
    {
        mBookingsRequestCompleted = false;
        mSwipeRefreshLayout.setRefreshing(true);
        bus.post(new BookingEvent.RequestBookings(Booking.List.VALUE_ONLY_BOOKINGS_UPCOMING));
    }

    private void bindBookingsToList()
    {
        if (mBookings != null)
        {
            //FIXME: remove this hard coding indexes
            ActiveBookingFragment frag = ActiveBookingFragment.newInstance(mBookings.get(0));
            frag.setParentScrollView(mScrollView);
            getChildFragmentManager().beginTransaction()
                    .replace(R.id.active_booking_container, frag, mBookings.get(0).getId())
                    .commit();

            mActiveBookingContainer.setVisibility(View.VISIBLE);

            mBookingsContainer.removeAllViews();
            for (int i = 1; i < mBookings.size(); i++)
            {
                Booking booking = mBookings.get(i);
                mBookingsContainer.addView(new BookingListItem(
                        getActivity(),
                        new View.OnClickListener()
                        {
                            @Override
                            public void onClick(final View v)
                            {
                                final Intent intent = new Intent(getActivity(), BookingDetailActivity.class);
                                Booking booking = ((BookingListItem) v).getBooking();
                                intent.putExtra(BundleKeys.BOOKING, booking);
                                getActivity().startActivityForResult(intent, ActivityResult.BOOKING_UPDATED);
                            }
                        },
                        mServices,
                        booking
                ));

                //add divider
                mBookingsContainer.addView(getActivity().getLayoutInflater().inflate(R.layout.layout_divider, mBookingsContainer, false));
            }

        }
    }

    @Subscribe
    public void onReceiveServicesError(final BookingEvent.ReceiveServicesError error)
    {
        //we don't really care that the services errored out, we just won't display the
        //services icon.
        mServiceRequestCompleted = true;
        setupBookingsView();
    }

    @Subscribe
    public void onReceiveBookingsSuccess(@NonNull BookingEvent.ReceiveBookingsSuccess event)
    {
        mBookingsRequestCompleted = true;
        Log.d(TAG, "onReceiveBookingsSuccess: " + event.getOnlyBookingsValue());
        mBookings = event.getBookingWrapper().getBookings();

        if (event.getBookingWrapper().getRecurringBookings() != null &&
                !event.getBookingWrapper().getRecurringBookings().isEmpty())
        {
            mRecurringBookings = event.getBookingWrapper().getRecurringBookings();
            filterRecurrencePlans();
            mActivePlanCount = mRecurringBookings.size();
        }
        else
        {
            mActivePlanCount = 0;
        }

        setupBookingsView();
    }

    /**
     * an active cleaning plan is a recurring plan that has at least one booking. If there are
     * plans with no booking, remove those.
     */
    private void filterRecurrencePlans()
    {
        for (int i = mRecurringBookings.size() - 1; i >= 0; i--)
        {
            UserRecurringBooking rb = mRecurringBookings.get(i);
            boolean found = false;
            if (mBookings != null)
            {
                for (Booking b : mBookings)
                {
                    if (b.getRecurringId() != null && String.valueOf(b.getRecurringId()).equals(rb.getId()))
                    {
                        found = true;
                        break;
                    }
                }
            }

            if (!found)
            {
                mRecurringBookings.remove(i);
            }
        }
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
        if (mBookingsRequestCompleted && mServiceRequestCompleted)
        {
            mSwipeRefreshLayout.setRefreshing(false);

            if (mActivePlanCount > 0)
            {
                mExpandableCleaningPlan.setVisibility(View.VISIBLE);
                mExpandableCleaningPlan.bind(new View.OnClickListener()
                                             {
                                                 @Override
                                                 public void onClick(final View v)
                                                 {
                                                     UserRecurringBooking rb = (UserRecurringBooking) v.getTag();
                                                     Booking booking = getBookingWithRecurringId(rb.getId());

                                                     if (booking != null)
                                                     {
                                                         final Intent intent = new Intent(UpcomingBookingsFragment.this.getActivity(), BookingEditFrequencyActivity.class);
                                                         intent.putExtra(BundleKeys.BOOKING, booking);
                                                         Activity activity = UpcomingBookingsFragment.this.getActivity();
                                                         activity.startActivityForResult(intent, ActivityResult.BOOKING_UPDATED);
                                                     }
                                                     else
                                                     {
                                                         //this should never happen, but just in case it does.
                                                         Toast.makeText(UpcomingBookingsFragment.this.getActivity(), R.string.unable_to_edit_plan, Toast.LENGTH_SHORT).show();
                                                     }
                                                 }
                                             },
                        mRecurringBookings,
                        getActiveCountString());
            }
            else
            {
                mExpandableCleaningPlan.setVisibility(View.GONE);
            }

            bindBookingsToList();

//        FIXME: JIA: only disable this when the mapview is turned on.
            if (mShouldShowMapView)
            {
                //if we're showing the map, disable this pull to refresh thing.
                mSwipeRefreshLayout.setEnabled(false);
            }
            else
            {

                //FIXME: make scrollview work with swipe to refresh.

//                mScrollView.setOnScrollChangeListener(new RecyclerView.OnScrollListener()
//                {
//                    @Override
//                    public void onScrolled(final RecyclerView recyclerView, final int dx, final int dy)
//                    {
//                        super.onScrolled(recyclerView, dx, dy);
//                        if (!recyclerView.canScrollVertically(-1))
//                        {
//                            mSwipeRefreshLayout.setEnabled(true);
//                        }
//                        else
//                        {
//                            mSwipeRefreshLayout.setEnabled(false);
//                        }
//                    }
//                });
            }
        }
    }

    /**
     * Takes in a recurring id, and finds the first booking that has such recurring id.
     *
     * @param id
     * @return
     */
    private Booking getBookingWithRecurringId(final String id)
    {
        for (Booking booking : mBookings)
        {
            if (booking.getRecurringId() != null && String.valueOf(booking.getRecurringId()).equals(id))
            {
                return booking;
            }
        }

        Crashlytics.logException(new RuntimeException("User requested to edit recurrence with id:"
                + id + " but there is no booking with such recurrence id."));

        return null;
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
