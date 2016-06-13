package com.handybook.handybook.module.proteam.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;

import com.handybook.handybook.R;
import com.handybook.handybook.module.proteam.model.ProTeam;
import com.handybook.handybook.module.proteam.ui.fragment.ProTeamFragment;
import com.handybook.handybook.ui.activity.MenuDrawerActivity;


public class ProTeamAddActivity extends MenuDrawerActivity
{

    private static final String KEY_PRO_TEAM = "ProTeamAddActivity:ProTeam";

    @Override
    protected Fragment createFragment()
    {
        ProTeam proTeam = getIntent().getExtras().getParcelable(KEY_PRO_TEAM);
        if (proTeam == null)
        {
            return ProTeamFragment.newInstance(ProTeamFragment.Mode.PRO_ADD);
        }
        else
        {
            return ProTeamFragment.newInstance(ProTeamFragment.Mode.PRO_ADD, proTeam);
        }
    }

    @Override
    protected String getNavItemTitle()
    {
        // This doesn't do anything, might as well be null
        return getString(R.string.title_activity_pro_team_add);
    }

    @Override
    protected boolean requiresUser()
    {
        return true;
    }

    public static Intent newIntent(final Context context, final ProTeam proTeam)
    {
        final Intent intent = new Intent(context, ProTeamAddActivity.class);
        intent.putExtra(KEY_PRO_TEAM, proTeam);
        return intent;
    }
}
