package com.handybook.handybook;

import android.support.v4.app.Fragment;

public final class BookingDetailActivity extends MenuDrawerActivity {
    @Override
    protected final Fragment createFragment() {
        return BookingDetailFragment.newInstance();
    }

    @Override
    protected final String getNavItemTitle() {
        return getString(R.string.booking);
    }
}
