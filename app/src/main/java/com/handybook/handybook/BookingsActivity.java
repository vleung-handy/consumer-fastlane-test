package com.handybook.handybook;

import android.support.v4.app.Fragment;

public final class BookingsActivity extends MenuDrawerActivity {
    @Override
    protected final Fragment createFragment() {
        return BookingsFragment.newInstance();
    }

    @Override
    protected final String getNavItemTitle() {
        return getString(R.string.my_bookings);
    }
}
