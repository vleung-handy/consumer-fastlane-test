package com.handybook.handybook.ui.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.handybook.handybook.core.Booking;
import com.handybook.handybook.core.BookingQuote;
import com.handybook.handybook.ui.fragment.PeakPricingFragment;

import java.util.ArrayList;

public final class PeakPricingActivity extends MenuDrawerActivity {
    public static final String EXTRA_RESCHEDULE_PRICE_TABLE = "com.handy.handy.EXTRA_RESCHEDULE_PRICE_TABLE";
    public static final String EXTRA_RESCHEDULE_BOOKING = "com.handy.handy.EXTRA_RESCHEDULE_BOOKING";
    public static final String EXTRA_RESCHEDULE_ALL = "com.handy.handy.EXTRA_RESCHEDULE_ALL";
    public static final String EXTRA_RESCHEDULE_NEW_DATE = "com.handy.handy.EXTRA_RESCHEDULE_NEW_DATE";
    public static final String EXTRA_FOR_VOUCHER = "com.handy.handy.EXTRA_FOR_VOUCHER";
    public static final int RESULT_RESCHEDULE_NEW_DATE = 2;

    @Override
    protected final Fragment createFragment() {
        final Booking rescheduleBooking = getIntent().getParcelableExtra(EXTRA_RESCHEDULE_BOOKING);
        final boolean rescheduleAll = getIntent().getBooleanExtra(EXTRA_RESCHEDULE_ALL, false);
        final boolean forVoucher = getIntent().getBooleanExtra(EXTRA_FOR_VOUCHER, false);

        final ArrayList<ArrayList<BookingQuote.PeakPriceInfo>> reschedulePriceTable
                = (ArrayList<ArrayList<BookingQuote.PeakPriceInfo>>) getIntent()
                .getSerializableExtra(EXTRA_RESCHEDULE_PRICE_TABLE);

        if (reschedulePriceTable != null) {
            return PeakPricingFragment.newInstance(reschedulePriceTable, rescheduleBooking,
                    rescheduleAll);
        }
        else return PeakPricingFragment.newInstance(forVoucher);
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
