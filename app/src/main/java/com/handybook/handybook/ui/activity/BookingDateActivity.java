package com.handybook.handybook.ui.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.handybook.handybook.core.Booking;
import com.handybook.handybook.core.BookingOption;
import com.handybook.handybook.ui.fragment.BookingDateFragment;

import java.util.ArrayList;

public final class BookingDateActivity extends MenuDrawerActivity {
    public static final String EXTRA_POST_OPTIONS = "com.handy.handy.EXTRA_POST_OPTIONS";
    public static final String EXTRA_RESCHEDULE_BOOKING = "com.handy.handy.EXTRA_RESCHEDULE_BOOKING";
    public static final String EXTRA_RESCHEDULE_NOTICE = "com.handy.handy.EXTRA_RESCHEDULE_NOTICE";
    public static final String EXTRA_RESCHEDULE_NEW_DATE = "com.handy.handy.EXTRA_RESCHEDULE_NEW_DATE";
    public static final int RESULT_RESCHEDULE_NEW_DATE = 1;

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
