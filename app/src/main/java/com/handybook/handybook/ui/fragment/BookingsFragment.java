package com.handybook.handybook.ui.fragment;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.handybook.handybook.R;
import com.handybook.handybook.ui.widget.ViewPager;

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

    public static final String FRAGMENT_TYPE_UPCOMING = "fragment_type_upcoming";
    public static final String FRAGMENT_TYPE_PAST = "fragment_type_past";

    private TabAdapter mTabAdapter;

    @Bind(R.id.pager)
    private ViewPager mPager;


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
        mTabAdapter = new TabAdapter(getChildFragmentManager());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        final View view =  inflater.inflate(R.layout.fragment_bookings_tabbed, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    private static class TabAdapter extends FragmentPagerAdapter
    {
        private ArrayList<BookingListFragment> fragments = new ArrayList<>();

        public TabAdapter(FragmentManager fm)
        {
            super(fm);
            fragments.add(
                    BookingListFragment.newInstance(BookingListFragment.BookingListType.UPCOMING)
            );
            fragments.add(
                    BookingListFragment.newInstance(BookingListFragment.BookingListType.PAST)
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
    }
}
