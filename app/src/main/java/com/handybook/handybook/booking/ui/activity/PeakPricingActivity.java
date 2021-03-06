package com.handybook.handybook.booking.ui.activity;

import android.support.v4.app.Fragment;

import com.handybook.handybook.booking.model.Booking;
import com.handybook.handybook.booking.model.PeakPriceInfo;
import com.handybook.handybook.booking.ui.fragment.PeakPricingFragment;
import com.handybook.handybook.core.constant.BundleKeys;
import com.handybook.handybook.core.ui.activity.SingleFragmentActivity;

import java.util.ArrayList;

public final class PeakPricingActivity extends SingleFragmentActivity {

    @Override
    protected final Fragment createFragment() {
        final Booking rescheduleBooking
                = getIntent().getParcelableExtra(BundleKeys.RESCHEDULE_BOOKING);
        final boolean rescheduleAll = getIntent().getBooleanExtra(BundleKeys.RESCHEDULE_ALL, false);
        final boolean forVoucher = getIntent().getBooleanExtra(BundleKeys.FOR_VOUCHER, false);

        final ArrayList<ArrayList<PeakPriceInfo>> reschedulePriceTable
                = (ArrayList<ArrayList<PeakPriceInfo>>) getIntent()
                .getSerializableExtra(BundleKeys.RESCHEDULE_PRICE_TABLE);

        if (reschedulePriceTable != null) {
            return PeakPricingFragment.newInstance(
                    reschedulePriceTable,
                    rescheduleBooking,
                    rescheduleAll,
                    getIntent().getExtras()
            );
        }
        else {
            return PeakPricingFragment.newInstance(forVoucher, getIntent().getExtras());
        }
    }
}
