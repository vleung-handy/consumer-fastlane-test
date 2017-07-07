package com.handybook.handybook.booking.history;

import android.support.v4.app.Fragment;

import com.handybook.handybook.core.ui.activity.SingleFragmentActivity;

public class HistoryActivity extends SingleFragmentActivity {

    @Override
    protected final Fragment createFragment() {
        return HistoryFragment.newInstance();
    }
}
