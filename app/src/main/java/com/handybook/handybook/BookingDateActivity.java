package com.handybook.handybook;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import java.util.ArrayList;

public final class BookingDateActivity extends MenuDrawerActivity {
    static final String EXTRA_POST_OPTIONS = "com.handy.handy.EXTRA_POST_OPTIONS";
    static final String EXTRA_RESCHEDULE_BOOKING = "com.handy.handy.EXTRA_RESCHEDULE_BOOKING";
    static final String EXTRA_RESCHEDULE_NOTICE = "com.handy.handy.EXTRA_RESCHEDULE_NOTICE";
    static final String EXTRA_RESCHEDULE_NEW_DATE = "com.handy.handy.EXTRA_RESCHEDULE_NEW_DATE";
    static final int RESULT_RESCHEDULE_NEW_DATE = 1;

    @Override
    protected final Fragment createFragment() {

        Booking rescheduleBooking = getIntent().getParcelableExtra(EXTRA_RESCHEDULE_BOOKING);
        if (rescheduleBooking != null) {
            final String notice = getIntent().getStringExtra(EXTRA_RESCHEDULE_NOTICE);
            return BookingDateFragment.newInstance(rescheduleBooking, notice);
        }

        final ArrayList<BookingOption> postOptions
                = getIntent().getParcelableArrayListExtra(EXTRA_POST_OPTIONS);
        return BookingDateFragment.newInstance(postOptions);
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
