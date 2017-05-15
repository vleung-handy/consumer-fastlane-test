package com.handybook.handybook.booking.history;

import android.support.v4.app.Fragment;

import com.handybook.handybook.R;
import com.handybook.handybook.core.ui.activity.MenuDrawerActivity;

public class HistoryActivity extends MenuDrawerActivity {

    @Override
    protected boolean requiresUser() {
        return true;
    }

    @Override
    protected final Fragment createFragment() {
        return HistoryFragment.newInstance();
    }

    @Override
    protected final String getNavItemTitle() {
        return getString(R.string.history);
    }

}
