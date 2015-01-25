package com.handybook.handybook.ui.activity;

import android.support.v4.app.Fragment;

import com.handybook.handybook.R;
import com.handybook.handybook.ui.fragment.HelpFragment;

public final class HelpActivity extends MenuDrawerActivity {

    @Override
    protected final Fragment createFragment() {
        return HelpFragment.newInstance();
    }

    @Override
    protected final String getNavItemTitle() {
        return getString(R.string.help);
    }
}
