package com.handybook.handybook.helpcenter.ui.activity;

import android.support.v4.app.Fragment;

import com.handybook.handybook.helpcenter.ui.fragment.HelpFragment;
import com.handybook.handybook.ui.activity.MenuDrawerActivity;

public class HelpActivity extends MenuDrawerActivity
{
    @Override
    protected Fragment createFragment()
    {
        return new HelpFragment();
    }

    @Override
    protected String getNavItemTitle()
    {
        return null;
    }
}
