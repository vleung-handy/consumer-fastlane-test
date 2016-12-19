package com.handybook.handybook.booking.ui.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.handybook.handybook.core.constant.BundleKeys;
import com.handybook.handybook.booking.model.Booking;
import com.handybook.handybook.booking.ui.fragment.BookingCancelOptionsFragment;
import com.handybook.handybook.core.ui.activity.MenuDrawerActivity;

import java.util.ArrayList;

public final class BookingCancelOptionsActivity extends MenuDrawerActivity
{

    @Override
    protected final Fragment createFragment() {
        final String notice = getIntent().getStringExtra(BundleKeys.NOTICE);
        final ArrayList<String> options = getIntent().getStringArrayListExtra(BundleKeys.OPTIONS);
        final Booking booking = getIntent().getParcelableExtra(BundleKeys.BOOKING);
        return BookingCancelOptionsFragment.newInstance(notice, options, booking);
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
