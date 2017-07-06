package com.handybook.handybook.account.ui;

import android.support.v4.app.Fragment;

import com.handybook.handybook.booking.model.RecurringBooking;
import com.handybook.handybook.core.constant.BundleKeys;
import com.handybook.handybook.core.ui.activity.SingleFragmentActivity;

public final class EditPlanAddressActivity extends SingleFragmentActivity {

    @Override
    protected final Fragment createFragment() {
        RecurringBooking recurringBooking =
                (RecurringBooking) getIntent().getSerializableExtra(BundleKeys.RECURRING_PLAN);
        return EditPlanAddressFragment.newInstance(recurringBooking);
    }
}
