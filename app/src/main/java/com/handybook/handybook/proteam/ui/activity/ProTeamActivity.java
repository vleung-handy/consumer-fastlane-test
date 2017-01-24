package com.handybook.handybook.proteam.ui.activity;

import android.support.v4.app.Fragment;

import com.handybook.handybook.R;
import com.handybook.handybook.core.constant.BundleKeys;
import com.handybook.handybook.core.ui.activity.MenuDrawerActivity;
import com.handybook.handybook.proteam.ui.fragment.ProTeamConversationsFragment;
import com.handybook.handybook.proteam.ui.fragment.ProTeamEditFragment;

public class ProTeamActivity extends MenuDrawerActivity
{
    @Override
    protected Fragment createFragment()
    {
        if (getIntent().getBooleanExtra(BundleKeys.PRO_TEAM_EDIT, false))
        {
            return ProTeamEditFragment.newInstance();
        }
        else
        {
            return ProTeamConversationsFragment.newInstance();
        }
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
