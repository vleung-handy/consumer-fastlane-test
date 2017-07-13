package com.handybook.handybook.booking.ui.activity;

import android.support.v4.app.Fragment;

import com.handybook.handybook.booking.ui.fragment.BookingLocationFragment;
import com.handybook.handybook.core.ui.activity.SingleFragmentActivity;

public final class BookingLocationActivity extends SingleFragmentActivity {

    @Override
    protected final Fragment createFragment() {
        return BookingLocationFragment.newInstance(getIntent().getExtras());
    }
}
