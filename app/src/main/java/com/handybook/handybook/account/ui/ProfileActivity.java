package com.handybook.handybook.account.ui;


import android.support.v4.app.Fragment;

import com.handybook.handybook.R;
import com.handybook.handybook.ui.activity.MenuDrawerActivity;

public final class ProfileActivity extends MenuDrawerActivity
{
    @Override
    protected boolean requiresUser()
    {
        return true;
    }

    @Override
    protected final Fragment createFragment()
    {
        if (getConfiguration().isNewAccountEnabled())
        {
            return AccountFragment.newInstance();
        }
        else
        {
            return ProfileFragment.newInstance();
        }
    }

    @Override
    protected final String getNavItemTitle()
    {
        return getString(R.string.account);
    }
}
