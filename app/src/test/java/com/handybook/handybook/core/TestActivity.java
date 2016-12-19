package com.handybook.handybook.core;

import android.support.v4.app.Fragment;

import com.handybook.handybook.core.ui.activity.MenuDrawerActivity;

public class TestActivity extends MenuDrawerActivity
{
    @Override
    protected Fragment createFragment()
    {
        return new Fragment();
    }

    @Override
    protected String getNavItemTitle()
    {
        return null;
    }
}
