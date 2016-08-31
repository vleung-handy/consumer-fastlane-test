package com.handybook.handybook.module.bookings;

import android.support.v4.app.Fragment;

import com.handybook.handybook.R;
import com.handybook.handybook.ui.activity.MenuDrawerActivity;

public class HistoryActivity extends MenuDrawerActivity
{

    @Override
    protected boolean requiresUser()
    {
        return true;
    }

    @Override
    protected final Fragment createFragment()
    {
        return new HistoryFragment();
    }

    @Override
    protected final String getNavItemTitle()
    {
        return getString(R.string.history);
    }

}
