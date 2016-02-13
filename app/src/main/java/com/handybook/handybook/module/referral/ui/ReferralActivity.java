package com.handybook.handybook.module.referral.ui;

import android.support.v4.app.Fragment;

import com.handybook.handybook.R;
import com.handybook.handybook.ui.activity.MenuDrawerActivity;

public class ReferralActivity extends MenuDrawerActivity
{
    @Override
    protected boolean requiresUser()
    {
        return true;
    }

    @Override
    protected Fragment createFragment()
    {
        return ReferralFragment.newInstance();
    }

    @Override
    protected String getNavItemTitle()
    {
        return getString(R.string.free_cleanings);
    }
}
