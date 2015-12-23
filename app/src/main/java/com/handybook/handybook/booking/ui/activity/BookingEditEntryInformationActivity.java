package com.handybook.handybook.booking.ui.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.handybook.handybook.constant.BundleKeys;
import com.handybook.handybook.booking.model.Booking;
import com.handybook.handybook.booking.ui.fragment.BookingEditEntryInformationFragment;
import com.handybook.handybook.ui.activity.MenuDrawerActivity;

public final class BookingEditEntryInformationActivity extends MenuDrawerActivity
{

    @Override
    protected final Fragment createFragment() {
        final Booking booking = getIntent().getParcelableExtra(BundleKeys.BOOKING);
        return BookingEditEntryInformationFragment.newInstance(booking);
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