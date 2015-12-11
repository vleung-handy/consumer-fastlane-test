package com.handybook.handybook.ui.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.handybook.handybook.ui.fragment.CancelRecurringBookingFragment;

public final class CancelRecurringBookingActivity extends MenuDrawerActivity
{
    //TODO: make the app swap fragments instead of launching a new activity for each one
    @Override
    protected final Fragment createFragment()
    {
        return CancelRecurringBookingFragment.newInstance();
    }

    @Override
    protected final String getNavItemTitle()
    {
        return null;
    }

    @Override
    protected void onCreate(final Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        disableDrawer = true;

    }
}
