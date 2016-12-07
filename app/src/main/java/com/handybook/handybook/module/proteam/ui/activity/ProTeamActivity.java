package com.handybook.handybook.module.proteam.ui.activity;

import android.support.v4.app.Fragment;

import com.handybook.handybook.R;
import com.handybook.handybook.constant.BundleKeys;
import com.handybook.handybook.module.proteam.ui.fragment.ProTeamConversationsFragment;
import com.handybook.handybook.module.proteam.ui.fragment.ProTeamEditFragment;
import com.handybook.handybook.module.proteam.ui.fragment.ProTeamFragment;
import com.handybook.handybook.ui.activity.MenuDrawerActivity;


public class ProTeamActivity extends MenuDrawerActivity
{
    @Override
    protected Fragment createFragment()
    {
        if (mConfigurationManager.getPersistentConfiguration().isChatEnabled())
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
        else
        {
            return ProTeamFragment.newInstance();
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
