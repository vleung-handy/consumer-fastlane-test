package com.handybook.handybook.account.ui;


import android.support.v4.app.Fragment;

import com.handybook.handybook.R;
import com.handybook.handybook.booking.model.RecurringBooking;
import com.handybook.handybook.core.constant.BundleKeys;
import com.handybook.handybook.core.ui.activity.MenuDrawerActivity;

public final class EditPlanFrequencyActivity extends MenuDrawerActivity
{
    @Override
    protected boolean requiresUser()
    {
        return true;
    }

    @Override
    protected final Fragment createFragment()
    {
        RecurringBooking recurringBooking =
                (RecurringBooking) getIntent().getSerializableExtra(BundleKeys.RECURRING_PLAN);
        return EditPlanFrequencyFragment.newInstance(recurringBooking);
    }

    @Override
    protected final String getNavItemTitle()
    {
        return getString(R.string.edit_frequency);
    }
}
