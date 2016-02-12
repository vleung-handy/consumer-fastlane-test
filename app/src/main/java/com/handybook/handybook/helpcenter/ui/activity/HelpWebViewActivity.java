package com.handybook.handybook.helpcenter.ui.activity;

import android.support.v4.app.Fragment;

import com.handybook.handybook.helpcenter.ui.fragment.HelpWebViewFragment;
import com.handybook.handybook.ui.activity.MenuDrawerActivity;

public class HelpWebViewActivity extends MenuDrawerActivity
{
    @Override
    protected Fragment createFragment()
    {
        return new HelpWebViewFragment();
    }

    @Override
    protected String getNavItemTitle()
    {
        return null;
    }
}
