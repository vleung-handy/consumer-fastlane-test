package com.handybook.handybook;

import android.os.Bundle;
import android.support.v4.app.Fragment;

public final class BookingRescheduleActivity extends MenuDrawerActivity {
    static final String EXTRA_BOOKING = "com.handy.handy.EXTRA_BOOKING";
    static final String EXTRA_NOTICE = "com.handy.handy.EXTRA_NOTICE";

    @Override
    protected final Fragment createFragment() {
        final Booking booking = getIntent().getParcelableExtra(EXTRA_BOOKING);
        final String notice = getIntent().getStringExtra(EXTRA_NOTICE);
        return BookingRescheduleFragment.newInstance(booking, notice);
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
