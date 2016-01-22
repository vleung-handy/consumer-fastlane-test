package com.handybook.handybook.module.referral.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.handybook.handybook.ui.activity.MenuDrawerActivity;

public class RedemptionActivity extends MenuDrawerActivity
{
    @Override
    protected void onCreate(final Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setDrawerDisabled(true);
    }

    @Override
    protected Fragment createFragment()
    {
        return RedemptionFragment.newInstance();
    }

    @Override
    protected String getNavItemTitle()
    {
        return null;
    }
}
