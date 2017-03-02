package com.handybook.handybook.booking.bookingedit.ui.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.handybook.handybook.booking.bookingedit.ui.fragment.BookingEditFrequencyFragment;
import com.handybook.handybook.booking.model.Booking;
import com.handybook.handybook.booking.model.RecurringBooking;
import com.handybook.handybook.core.constant.BundleKeys;
import com.handybook.handybook.core.ui.activity.MenuDrawerActivity;

/**
 * NOTE: doesn't look like this is being used anymore
 */
@Deprecated
public final class BookingEditFrequencyActivity extends MenuDrawerActivity {

    @Override
    protected final Fragment createFragment() {
        final Booking booking = getIntent().getParcelableExtra(BundleKeys.BOOKING);
        final RecurringBooking recurringBooking
                = (RecurringBooking) getIntent().getSerializableExtra(
                BundleKeys.RECURRING_BOOKING);

        //note that only one of the two parameters will be used.
        return BookingEditFrequencyFragment.newInstance(booking, recurringBooking);
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
