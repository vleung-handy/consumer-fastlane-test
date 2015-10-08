package com.handybook.handybook.ui.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.handybook.handybook.constant.BundleKeys;
import com.handybook.handybook.core.Booking;
import com.handybook.handybook.core.BookingOption;
import com.handybook.handybook.ui.fragment.BookingDateFragment;

import java.util.ArrayList;

public final class BookingDateActivity extends MenuDrawerActivity {

    @Override
    protected final Fragment createFragment() {

        Booking rescheduleBooking = getIntent().getParcelableExtra(BundleKeys.RESCHEDULE_BOOKING);
        if (rescheduleBooking != null) {
            final String notice = getIntent().getStringExtra(BundleKeys.RESCHEDULE_NOTICE);
            return BookingDateFragment.newInstance(rescheduleBooking, notice);
        }

        final ArrayList<BookingOption> postOptions
                = getIntent().getParcelableArrayListExtra(BundleKeys.POST_OPTIONS);
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
