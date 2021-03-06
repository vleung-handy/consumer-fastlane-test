package com.handybook.handybook.booking.bookingedit.ui.activity;

import android.support.v4.app.Fragment;

import com.handybook.handybook.booking.bookingedit.ui.fragment.BookingEditHoursFragment;
import com.handybook.handybook.booking.model.Booking;
import com.handybook.handybook.core.constant.BundleKeys;
import com.handybook.handybook.core.ui.activity.SingleFragmentActivity;

public final class BookingEditHoursActivity extends SingleFragmentActivity {

    @Override
    protected final Fragment createFragment() {
        final Booking booking = getIntent().getParcelableExtra(BundleKeys.BOOKING);
        return BookingEditHoursFragment.newInstance(booking);
    }
}
