package com.handybook.handybook.booking.ui.activity;

import android.support.v4.app.Fragment;

import com.handybook.handybook.R;
import com.handybook.handybook.booking.ui.fragment.BookingsFragment;
import com.handybook.handybook.ui.activity.MenuDrawerActivity;

public final class BookingsActivity extends MenuDrawerActivity
{

    @Override
    protected final Fragment createFragment() {
        return BookingsFragment.newInstance();
    }

    @Override
    protected final String getNavItemTitle() {
        return getString(R.string.my_bookings);
    }

}
