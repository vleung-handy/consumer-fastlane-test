package com.handybook.handybook.booking.ui.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.handybook.handybook.booking.model.Booking;
import com.handybook.handybook.booking.ui.fragment.BookingRescheduleOptionsFragment;
import com.handybook.handybook.constant.BundleKeys;
import com.handybook.handybook.ui.activity.MenuDrawerActivity;

import java.util.Date;

public final class BookingRescheduleOptionsActivity extends MenuDrawerActivity
{

    @Override
    protected final Fragment createFragment() {
        final Booking rescheduleBooking = getIntent().getParcelableExtra(BundleKeys.RESCHEDULE_BOOKING);
        final Date date = new Date(getIntent().getLongExtra(BundleKeys.RESCHEDULE_NEW_DATE, 0));
        final String providerId = getIntent().getStringExtra(BundleKeys.PROVIDER_ID);
        return BookingRescheduleOptionsFragment.newInstance(rescheduleBooking, date, providerId);
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
