package com.handybook.handybook;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import java.util.ArrayList;

public final class PeakPricingActivity extends MenuDrawerActivity {
    static final String EXTRA_RESCHEDULE_PRICE_TABLE = "com.handy.handy.EXTRA_RESCHEDULE_PRICE_TABLE";
    static final String EXTRA_RESCHEDULE_BOOKING = "com.handy.handy.EXTRA_RESCHEDULE_BOOKING";
    static final String EXTRA_RESCHEDULE_ALL = "com.handy.handy.EXTRA_RESCHEDULE_ALL";
    static final String EXTRA_RESCHEDULE_NEW_DATE = "com.handy.handy.EXTRA_RESCHEDULE_NEW_DATE";
    static final int RESULT_RESCHEDULE_NEW_DATE = 2;

    @Override
    protected final Fragment createFragment() {
        final Booking rescheduleBooking = getIntent().getParcelableExtra(EXTRA_RESCHEDULE_BOOKING);
        final boolean rescheduleAll = getIntent().getBooleanExtra(EXTRA_RESCHEDULE_ALL, false);

        final ArrayList<ArrayList<BookingQuote.PeakPriceInfo>> reschedulePriceTable
                = (ArrayList<ArrayList<BookingQuote.PeakPriceInfo>>) getIntent()
                .getSerializableExtra(EXTRA_RESCHEDULE_PRICE_TABLE);

        if (reschedulePriceTable != null) {
            return PeakPricingFragment.newInstance(reschedulePriceTable, rescheduleBooking, rescheduleAll);
        }
        else return PeakPricingFragment.newInstance();
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
