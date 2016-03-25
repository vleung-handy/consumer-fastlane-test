package com.handybook.handybook.booking.ui.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.handybook.handybook.R;
import com.handybook.handybook.analytics.MixpanelEvent;
import com.handybook.handybook.booking.BookingEvent;
import com.handybook.handybook.booking.model.Service;
import com.handybook.handybook.booking.ui.view.ServiceCategoriesOverlayFragment;
import com.handybook.handybook.booking.viewmodel.BookingCardViewModel;
import com.handybook.handybook.constant.ActivityResult;
import com.handybook.handybook.ui.activity.MenuDrawerActivity;
import com.handybook.handybook.ui.fragment.InjectedFragment;
import com.handybook.handybook.ui.view.HandyTabLayout;
import com.handybook.handybook.util.UiUtils;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * A simple {@link InjectedFragment} subclass.
 * Use the {@link BookingsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BookingsFragment extends InjectedFragment
{
    @Bind(R.id.pager)
    ViewPager mViewPager;
    @Bind(R.id.tab_layout)
    HandyTabLayout mTabLayout;
    @Bind(R.id.add_booking_button)
    FloatingActionButton mAddBookingButton;

    @Bind(R.id.toolbar)
    Toolbar mToolbar;

    private TabAdapter mTabAdapter;
    private List<Service> mServices;

    public static final String mOverlayFragmentTag = ServiceCategoriesOverlayFragment.class.getSimpleName();

    public BookingsFragment()
    {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment BookingsFragmentTabbed.
     */
    public static BookingsFragment newInstance()
    {
        return new BookingsFragment();
    }

    @Override
    public void onActivityResult(final int requestCode, final int resultCode, final Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == ActivityResult.BOOKING_UPDATED
                || resultCode == ActivityResult.BOOKING_CANCELED)
        {
            mTabAdapter.reloadFragments();
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        mTabAdapter = new TabAdapter(getActivity(), getChildFragmentManager());
    }

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    )
    {
        final View view = inflater.inflate(R.layout.fragment_bookings, container, false);
        ButterKnife.bind(this, view);

        setupToolbar(mToolbar, getString(R.string.my_bookings));
        ((MenuDrawerActivity) getActivity()).setupHamburgerMenu(mToolbar);

        mViewPager.setAdapter(mTabAdapter);
        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(mTabLayout));
        mTabLayout.setupWithViewPager(mViewPager);
        mTabLayout.setTabsFromPagerAdapter(mTabAdapter);

        bus.post(new BookingEvent.RequestServices());

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
        return view;
    }

    @Subscribe
    public void onReceiveServicesSuccess(final BookingEvent.ReceiveServicesSuccess event)
    {
        mServices = event.getServices();
        if (mServices != null)
        {
            UiUtils.revealView(mAddBookingButton);
        }
    }

    @Subscribe
    public void onReceiveCachedServicesSuccess(final BookingEvent.ReceiveCachedServicesSuccess event)
    {
        mServices = event.getServices();
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

    private static class TabAdapter extends FragmentPagerAdapter
    {
        private ArrayList<BookingListFragment> fragments = new ArrayList<>();
        private ArrayList<String> titles = new ArrayList<>();

        public TabAdapter(Context context, FragmentManager fm)
        {
            super(fm);

            titles.add(context.getResources().getString(R.string.upcoming));
            fragments.add(
                    BookingListFragment.newInstance(BookingCardViewModel.List.TYPE_UPCOMING)
            );
            titles.add(context.getResources().getString(R.string.past));
            fragments.add(
                    BookingListFragment.newInstance(BookingCardViewModel.List.TYPE_PAST)
            );
        }

        public void reloadFragments()
        {
            for (BookingListFragment fragment : fragments)
            {
                if (fragment.isVisible())
                {
                    fragment.loadBookings();
                }
            }
        }

        @Override
        public int getCount()
        {
            return fragments.size();
        }

        @Override
        public CharSequence getPageTitle(int position)
        {
            return titles.get(position);
        }

        @Override
        public BookingListFragment getItem(int position)
        {
            return fragments.get(position);
        }
    }
}
