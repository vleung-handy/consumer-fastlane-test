package com.handybook.handybook.ui.fragment;

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

    private TabAdapter mTabAdapter;

    @Bind(R.id.menu_button_layout)
    ViewGroup vMenuButtonLayout;
    @Bind(R.id.pager)
    ViewPager vViewPager;
    @Bind(R.id.tab_layout)
    TabLayout vTabLayout;


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

    public BookingsFragment()
    {
        // Required empty public constructor
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
        vViewPager.setAdapter(mTabAdapter);
        vViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(vTabLayout));
        vTabLayout.setupWithViewPager(vViewPager);
        vTabLayout.setTabsFromPagerAdapter(mTabAdapter);
        final MenuButton menuButton = new MenuButton(getActivity(), vMenuButtonLayout);
        vMenuButtonLayout.addView(menuButton);

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
                    BookingListFragment.newInstance(BookingListFragment.TYPE_UPCOMING)
            );
            titles.add(context.getResources().getString(R.string.past));
            fragments.add(
                    BookingListFragment.newInstance(BookingListFragment.TYPE_PAST)
            );
        }

        @Override
        public int getCount()
        {
            return fragments.size();
        }

        @Override
        public BookingListFragment getItem(int position)
        {
            return fragments.get(position);
        }

        @Override
        public CharSequence getPageTitle(int position)
        {
            return titles.get(position);
        }
    }
}
