package com.handybook.handybook.booking.ui.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.handybook.handybook.booking.model.Booking;
import com.handybook.handybook.booking.model.BookingCancellationData;
import com.handybook.handybook.booking.ui.fragment.BookingCancelReasonFragment;
import com.handybook.handybook.booking.ui.fragment.BookingCancelWarningFragment;
import com.handybook.handybook.core.constant.BundleKeys;
import com.handybook.handybook.core.ui.activity.MenuDrawerActivity;

public final class BookingCancelOptionsActivity extends MenuDrawerActivity {

    @Override
    protected final Fragment createFragment() {
        final BookingCancellationData bookingCancellationData
                = (BookingCancellationData) getIntent()
                .getSerializableExtra(BundleKeys.BOOKING_CANCELLATION_DATA);
        final Booking booking = getIntent().getParcelableExtra(BundleKeys.BOOKING);
        if (bookingCancellationData.hasPrecancellationInfo()) {
            return BookingCancelWarningFragment.newInstance(booking, bookingCancellationData);
        }
        else {
            return BookingCancelReasonFragment.newInstance(booking, bookingCancellationData);
        }
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
