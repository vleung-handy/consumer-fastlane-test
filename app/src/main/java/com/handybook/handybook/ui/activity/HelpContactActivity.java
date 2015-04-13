package com.handybook.handybook.ui.activity;

import android.support.v4.app.Fragment;

import com.handybook.handybook.R;
import com.handybook.handybook.core.HelpNode;
import com.handybook.handybook.ui.fragment.HelpContactFragment;
import com.handybook.handybook.ui.fragment.HelpFragment;

public final class HelpContactActivity extends MenuDrawerActivity {

    @Override
    protected final Fragment createFragment() {
        return HelpContactFragment.newInstance();
    }

    @Override
    protected final String getNavItemTitle() {
        return "";
    }
}
