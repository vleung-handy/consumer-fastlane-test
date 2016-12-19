package com.handybook.handybook.booking.ui.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.handybook.handybook.booking.model.Booking;
import com.handybook.handybook.booking.ui.fragment.BookingDetailFragment;
import com.handybook.handybook.core.constant.BundleKeys;
import com.handybook.handybook.core.ui.activity.MenuDrawerActivity;

public final class BookingDetailActivity extends MenuDrawerActivity
{

    @Override
    protected final Fragment createFragment()
    {
        final Booking booking = getIntent().getParcelableExtra(BundleKeys.BOOKING);
        final boolean isFromBookingFlow = getIntent()
                .getBooleanExtra(BundleKeys.IS_FROM_BOOKING_FLOW, false);
        if (booking != null)
        {
            return BookingDetailFragment.newInstance(booking, isFromBookingFlow);
        }
        else
        {
            final String bookingId = getIntent().getStringExtra(BundleKeys.BOOKING_ID);
            return BookingDetailFragment.newInstance(bookingId, isFromBookingFlow);
        }
    }

    @Override
    protected final String getNavItemTitle()
    {
        return null;
    }

    @Override
    protected void onCreate(final Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        disableDrawer = true;
    }
}
