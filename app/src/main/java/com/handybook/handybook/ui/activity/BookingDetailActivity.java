package com.handybook.handybook.ui.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.handybook.handybook.core.Booking;
import com.handybook.handybook.ui.fragment.BookingDetailFragment;

public final class BookingDetailActivity extends MenuDrawerActivity {
    public static final String EXTRA_BOOKING = "com.handy.handy.EXTRA_BOOKING";
    public static final String EXTRA_UPDATED_BOOKING = "com.handy.handy.EXTRA_UPDATED_BOOKING";
    public static final String EXTRA_CANCELED_BOOKING = "com.handy.handy.EXTRA_CANCELED_BOOKING";
    public static final int RESULT_BOOKING_UPDATED = 1;
    public static final int RESULT_BOOKING_CANCELED = 2;

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
