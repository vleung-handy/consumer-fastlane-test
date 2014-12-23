package com.handybook.handybook;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;

public final class BookingDetailActivity extends MenuDrawerActivity {
    static final String EXTRA_BOOKING = "com.handy.handy.EXTRA_BOOKING";
    static final String EXTRA_UPDATED_BOOKING = "com.handy.handy.EXTRA_UPDATED_BOOKING";
    static final int RESULT_BOOKING_UPDATED = 1;

    @Override
    protected final Fragment createFragment() {
        final Booking booking = getIntent().getParcelableExtra(EXTRA_BOOKING);
        return BookingDetailFragment.newInstance(booking);
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

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode,
                                    final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }
}
