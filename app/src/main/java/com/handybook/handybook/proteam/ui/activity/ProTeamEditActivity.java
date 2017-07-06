package com.handybook.handybook.proteam.ui.activity;

import android.support.v4.app.Fragment;

import com.handybook.handybook.core.ui.activity.SingleFragmentActivity;
import com.handybook.handybook.proteam.ui.fragment.ProTeamEditFragment;

public class ProTeamEditActivity extends SingleFragmentActivity {

    @Override
    protected Fragment createFragment() {
        return ProTeamEditFragment.newInstance();
    }
}
