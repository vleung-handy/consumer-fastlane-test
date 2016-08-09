package com.handybook.handybook.module.bookings;

import android.content.Context;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.Log;

import com.handybook.handybook.R;

import java.util.ArrayList;

/**
 */
public class TabAdapter extends FragmentPagerAdapter
{

    private static final String TAG = TabAdapter.class.getName();
    private ArrayList<BookingListFragment> fragments = new ArrayList<>();
    private ArrayList<String> titles = new ArrayList<>();

    public TabAdapter(Context context, FragmentManager fm)
    {
        super(fm);

        titles.add(context.getResources().getString(R.string.upcoming));
        fragments.add(
                BookingListFragment.newInstance(ListType.UPCOMING)
        );
        titles.add(context.getResources().getString(R.string.past));
        fragments.add(
                BookingListFragment.newInstance(ListType.PAST)
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
        Log.d(TAG, "getItem: " + position);
        return fragments.get(position);
    }
}
