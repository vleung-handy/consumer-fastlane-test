package com.handybook.handybook.booking.ui.activity;

import android.support.v4.app.Fragment;

import com.handybook.handybook.booking.ui.fragment.BookingExtrasFragment;
import com.handybook.handybook.core.ui.activity.SingleFragmentActivity;

public final class BookingExtrasActivity extends SingleFragmentActivity {

    @Override
    protected final Fragment createFragment() {
        return BookingExtrasFragment.newInstance(getIntent().getExtras());
    }
}
