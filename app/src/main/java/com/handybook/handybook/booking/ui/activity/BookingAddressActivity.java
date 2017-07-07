package com.handybook.handybook.booking.ui.activity;

import android.support.v4.app.Fragment;

import com.handybook.handybook.booking.ui.fragment.BookingAddressFragment;
import com.handybook.handybook.core.ui.activity.SingleFragmentActivity;

public final class BookingAddressActivity extends SingleFragmentActivity {

    @Override
    protected final Fragment createFragment() {
        return BookingAddressFragment.newInstance(getIntent().getExtras());
    }
}
