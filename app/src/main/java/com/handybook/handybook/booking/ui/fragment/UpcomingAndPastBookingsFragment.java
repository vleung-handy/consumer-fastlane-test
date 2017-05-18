package com.handybook.handybook.booking.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.handybook.handybook.R;
import com.handybook.handybook.booking.history.HistoryFragment;
import com.handybook.handybook.library.ui.fragment.InjectedFragment;
import com.handybook.handybook.logger.handylogger.LogEvent;
import com.handybook.handybook.logger.handylogger.model.AppLog;
import com.handybook.handybook.logger.handylogger.model.booking.BookingsLog;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * currently this will show in {@link com.handybook.handybook.booking.ui.activity.BookingsActivity}
 * and the "my bookings" tab instead of {@link UpcomingBookingsFragment} if the relevant config is on
 */
public class UpcomingAndPastBookingsFragment extends InjectedFragment {

    @Bind(R.id.toolbar)
    Toolbar mToolbar;

    @Bind(R.id.upcoming_and_past_bookings_tab_layout)
    TabLayout mTabLayout;

    @Bind(R.id.upcoming_and_past_bookings_viewpager)
    ViewPager mViewPager;

    public UpcomingAndPastBookingsFragment() {
        // Required empty public constructor
    }

    public static UpcomingAndPastBookingsFragment newInstance() {
        return new UpcomingAndPastBookingsFragment();
    }

    @Nullable
    @Override
    public View onCreateView(
            final LayoutInflater inflater,
            @Nullable final ViewGroup container,
            @Nullable final Bundle savedInstanceState
    ) {
        final View view = inflater.inflate(R.layout.fragment_upcoming_and_past_bookings, container, false);
        ButterKnife.bind(this, view);

        setupToolbar(mToolbar, getString(R.string.my_bookings));
        mToolbar.setNavigationIcon(null);

        initTabs();
        return view;
    }

    private void initTabs()
    {
        mTabLayout.removeAllTabs();

        //for logging purposes only
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @BookingsLog.PageToggled.BookingsLogPage
            private String getPageSelectedForTabPosition(int position) {
                switch (position) {
                    case TabAdapter.UPCOMING_BOOKINGS_TAB_POSITION:
                        return BookingsLog.PageToggled.Page.UPCOMING_BOOKINGS;
                    case TabAdapter.PAST_BOOKINGS_TAB_POSITION:
                        return BookingsLog.PageToggled.Page.PAST_BOOKINGS;
                }
                return null;
            }

            @Override
            public void onPageScrolled(
                    final int position,
                    final float positionOffset,
                    final int positionOffsetPixels
            ) {
            }

            @Override
            public void onPageSelected(final int position) {
                //this is triggered when the user swipes to this page or selects the relevant tab
                bus.post(new LogEvent.AddLogEvent(new BookingsLog.PageToggled(
                        getPageSelectedForTabPosition(position)
                )));
            }

            @Override
            public void onPageScrollStateChanged(final int state) {

            }
        });
        TabAdapter tabAdapter = new TabAdapter(getChildFragmentManager());
        mViewPager.setAdapter(tabAdapter);
        mTabLayout.setupWithViewPager(mViewPager);

        //select the upcoming bookings tab by default
        mViewPager.setCurrentItem(TabAdapter.UPCOMING_BOOKINGS_TAB_POSITION);
        //log the bookings tab that is initially shown
        bus.post(new LogEvent.AddLogEvent(new AppLog.AppNavigationLog(
                AppLog.AppNavigationLog.Page.UPCOMING_BOOKINGS
        )));
    }

    /**
     * dont want to store both fragments in memory at the same time
     * and dont want them to load until switched into
     */
    private class TabAdapter extends android.support.v4.app.FragmentStatePagerAdapter {

        private static final int NUM_ITEMS = 2;
        static final int UPCOMING_BOOKINGS_TAB_POSITION = 0;
        static final int PAST_BOOKINGS_TAB_POSITION = 1;

        TabAdapter(final FragmentManager fragmentManager) {
            super(fragmentManager);
        }

        @Override
        public CharSequence getPageTitle(final int position) {
            switch(position)
            {
                case UPCOMING_BOOKINGS_TAB_POSITION:
                    return getResources().getString(R.string.upcoming);
                case PAST_BOOKINGS_TAB_POSITION:
                    return getResources().getString(R.string.past);
                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            return NUM_ITEMS;
        }

        @Override
        public InjectedFragment getItem(int position) {
            switch (position) {
                case UPCOMING_BOOKINGS_TAB_POSITION:
                    return UpcomingBookingsFragment.newInstance(false);
                case PAST_BOOKINGS_TAB_POSITION:
                    return HistoryFragment.newInstance(false);
                default:
                    return null;
            }
        }
    }
}
