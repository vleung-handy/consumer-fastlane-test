package com.handybook.handybook.booking.ui.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.handybook.handybook.R;
import com.handybook.handybook.ui.fragment.InjectedFragment;
import com.handybook.handybook.booking.viewmodel.BookingCardViewModel;
import com.handybook.handybook.ui.view.HandyTabLayout;
import com.handybook.handybook.ui.widget.MenuButton;

import java.util.ArrayList;

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
        BookingsFragment fragment = new BookingsFragment();
        return fragment;
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

        return view;
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
