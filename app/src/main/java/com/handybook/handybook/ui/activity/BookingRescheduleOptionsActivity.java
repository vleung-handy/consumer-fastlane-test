package com.handybook.handybook.ui.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.handybook.handybook.core.Booking;
import com.handybook.handybook.ui.fragment.BookingRescheduleOptionsFragment;

import java.util.Date;

public final class BookingRescheduleOptionsActivity extends MenuDrawerActivity {
    public static final String EXTRA_RESCHEDULE_BOOKING = "com.handy.handy.EXTRA_RESCHEDULE_BOOKING";
    public static final String EXTRA_RESCHEDULE_DATE = "com.handy.handy.EXTRA_RESCHEDULE_DATE";
    public static final String EXTRA_RESCHEDULE_NEW_DATE = "com.handy.handy.EXTRA_RESCHEDULE_NEW_DATE";

    @Override
    protected final Fragment createFragment() {
        final Booking rescheduleBooking = getIntent().getParcelableExtra(EXTRA_RESCHEDULE_BOOKING);
        final Date date = new Date(getIntent().getLongExtra(EXTRA_RESCHEDULE_DATE, 0));
        return BookingRescheduleOptionsFragment.newInstance(rescheduleBooking, date);
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
