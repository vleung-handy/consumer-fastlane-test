package com.handybook.handybook.module.proteam.ui.activity;

import android.support.v4.app.Fragment;

import com.handybook.handybook.module.proteam.ui.fragment.ProTeamFragment;
import com.handybook.handybook.ui.activity.MenuDrawerActivity;


public class ProTeamActivity extends MenuDrawerActivity
{

    @Override
    protected Fragment createFragment()
    {
        return ProTeamFragment.newInstance();
    }

    @Override
    protected String getNavItemTitle()
    {
        return null;
    }

    @Override
    protected boolean requiresUser()
    {
        return true;
    }

}
