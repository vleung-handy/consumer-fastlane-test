package com.handybook.handybook.proteam.ui.activity;

import android.support.v4.app.Fragment;

import com.handybook.handybook.R;
import com.handybook.handybook.core.ui.activity.MenuDrawerActivity;
import com.handybook.handybook.proteam.ui.fragment.ProTeamConversationsFragment;

public class ProTeamActivity extends MenuDrawerActivity {

    @Override
    protected Fragment createFragment() {
        return ProTeamConversationsFragment.newInstance();
    }

    @Override
    protected String getNavItemTitle() {
        // This doesn't do anything, might as well be null
        return getString(R.string.title_activity_pro_team);
    }

    @Override
    protected boolean requiresUser() {
        return true;
    }
}
