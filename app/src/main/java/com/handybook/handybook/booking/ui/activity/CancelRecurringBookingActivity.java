package com.handybook.handybook.booking.ui.activity;

import android.support.v4.app.Fragment;

import com.handybook.handybook.booking.ui.fragment.CancelRecurringBookingSelectionFragment;
import com.handybook.handybook.core.ui.activity.SingleFragmentActivity;

public final class CancelRecurringBookingActivity extends SingleFragmentActivity {

    //TODO: make the app swap fragments instead of launching a new activity for each one
    @Override
    protected final Fragment createFragment() {
        return CancelRecurringBookingSelectionFragment.newInstance();
    }
}
