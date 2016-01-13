package com.handybook.handybook.booking.ui.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.handybook.handybook.R;
import com.handybook.handybook.booking.model.Service;
import com.handybook.handybook.booking.ui.activity.ServiceCategoriesActivity;
import com.handybook.handybook.booking.ui.view.ServiceCategoriesOverlayFragment;
import com.handybook.handybook.booking.viewmodel.BookingCardViewModel;
import com.handybook.handybook.constant.ActivityResult;
import com.handybook.handybook.event.HandyEvent;
import com.handybook.handybook.ui.fragment.InjectedFragment;
import com.handybook.handybook.ui.view.HandyTabLayout;
import com.handybook.handybook.ui.widget.MenuButton;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * A simple {@link InjectedFragment} subclass.
 * Use the {@link BookingsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BookingsFragment extends InjectedFragment
{
    @Bind(R.id.menu_button_layout)
    ViewGroup mMenuButtonLayout;
    @Bind(R.id.pager)
    ViewPager mViewPager;
    @Bind(R.id.tab_layout)
    HandyTabLayout mTabLayout;
    private TabAdapter mTabAdapter;
    private List<Service> mServices;

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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        final View view = inflater.inflate(R.layout.fragment_bookings, container, false);
        ButterKnife.bind(this, view);
        mViewPager.setAdapter(mTabAdapter);
        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(mTabLayout));
        mTabLayout.setupWithViewPager(mViewPager);
        mTabLayout.setTabsFromPagerAdapter(mTabAdapter);
        final MenuButton menuButton = new MenuButton(getActivity(), mMenuButtonLayout);
        menuButton.setColor(getResources().getColor(R.color.white));
        mMenuButtonLayout.addView(menuButton);
        bus.post(new HandyEvent.RequestServices());

        return view;
    }

    @Subscribe
    public void onReceiveServicesSuccess(final HandyEvent.ReceiveServicesSuccess event)
    {
        mServices = event.getServices();
    }

    @Subscribe
    public void onReceiveCachedServicesSuccess(final HandyEvent.ReceiveCachedServicesSuccess event)
    {
        mServices = event.getServices();
    }

    @Subscribe
    public void onAddBookingButtonClicked(HandyEvent.AddBookingButtonClicked event)
    {
        if (mServices != null)
        {
            final FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            final String tag = ServiceCategoriesOverlayFragment.class.getSimpleName();
            if (fragmentManager.findFragmentByTag(tag) == null)
            {
                fragmentManager.beginTransaction()
                        .setCustomAnimations(R.anim.fade_in, 0, 0, R.anim.fade_out)
                        .add(R.id.fragment_container,
                                ServiceCategoriesOverlayFragment.newInstance(mServices),
                                tag)
                        .addToBackStack(null)
                        .commit();
            }
        }
        else
        {
            startActivity(new Intent(getActivity(), ServiceCategoriesActivity.class));
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
