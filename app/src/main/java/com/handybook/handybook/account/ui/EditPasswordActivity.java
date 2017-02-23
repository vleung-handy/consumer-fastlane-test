package com.handybook.handybook.account.ui;


import android.support.v4.app.Fragment;

import com.handybook.handybook.R;
import com.handybook.handybook.core.ui.activity.MenuDrawerActivity;

public final class EditPasswordActivity extends MenuDrawerActivity
{
    @Override
    protected boolean requiresUser()
    {
        return true;
    }

    @Override
    protected final Fragment createFragment()
    {
        return ProfilePasswordFragment.newInstance();
    }

    @Override
    protected final String getNavItemTitle()
    {
        return getString(R.string.account_update_password);
    }
}