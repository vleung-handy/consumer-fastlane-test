package com.handybook.handybook.ui.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.handybook.handybook.constant.BundleKeys;
import com.handybook.handybook.core.Booking;
import com.handybook.handybook.ui.fragment.BookingCancelOptionsFragment;

import java.util.ArrayList;

public final class BookingCancelOptionsActivity extends MenuDrawerActivity {

    public static final int RESULT_BOOKING_CANCELED = 10;

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
