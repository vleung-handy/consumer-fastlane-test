package com.handybook.handybook;

import android.os.Bundle;
import android.support.v4.app.Fragment;

public final class BookingDetailActivity extends MenuDrawerActivity {
    static final String EXTRA_BOOKING = "com.handy.handy.EXTRA_BOOKING";

    @Override
    protected final Fragment createFragment() {
        final Booking booking = getIntent().getParcelableExtra(EXTRA_BOOKING);
        return BookingDetailFragment.newInstance(booking);
    }

    @Override
    protected final String getNavItemTitle() {
        return null;
    }

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        disableDrawer = true;
    }
}
