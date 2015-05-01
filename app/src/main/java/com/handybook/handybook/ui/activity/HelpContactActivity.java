package com.handybook.handybook.ui.activity;

import android.support.v4.app.Fragment;

import com.handybook.handybook.R;
import com.handybook.handybook.core.HelpNode;
import com.handybook.handybook.ui.fragment.HelpContactFragment;
import com.handybook.handybook.ui.fragment.HelpFragment;

public final class HelpContactActivity extends MenuDrawerActivity {

    public static final String EXTRA_HELP_NODE = "com.handy.handy.EXTRA_HELP_NODE";
    public static final String EXTRA_HELP_PATH = "com.handy.handy.EXTRA_PATH";

    @Override
    protected final Fragment createFragment() {
        final HelpNode node = getIntent().getParcelableExtra(EXTRA_HELP_NODE);
        final String path = getIntent().getStringExtra(EXTRA_HELP_PATH);
        return HelpContactFragment.newInstance(node, path);
    }

    @Override
    protected final String getNavItemTitle() {
        return "";
    }
}
