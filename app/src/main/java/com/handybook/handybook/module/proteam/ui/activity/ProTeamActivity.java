package com.handybook.handybook.module.proteam.ui.activity;

import android.support.v4.app.Fragment;

import com.handybook.handybook.R;
import com.handybook.handybook.module.proteam.ui.fragment.ProTeamFragment;
import com.handybook.handybook.ui.activity.MenuDrawerActivity;


public class ProTeamActivity extends MenuDrawerActivity
{

    @Override
    protected Fragment createFragment()
    {
        return ProTeamFragment.newInstance(ProTeamFragment.Mode.PRO_MANAGE);
    }

    @Override
    protected String getNavItemTitle()
    {
        // This doesn't do anything, might as well be null
        return getString(R.string.title_activity_pro_team);
    }

    @Override
    protected boolean requiresUser()
    {
        return true;
    }

}
