package com.handybook.handybook.ui.activity;

import android.support.v4.app.Fragment;

import com.handybook.handybook.R;
import com.handybook.handybook.core.HelpNode;
import com.handybook.handybook.ui.fragment.HelpFragment;

public final class HelpActivity extends MenuDrawerActivity {
    public static final String EXTRA_HELP_NODE = "com.handy.handy.EXTRA_HELP_NODE";
    public static final String EXTRA_BOOKING_ID = "com.handy.handy.EXTRA_BOOKING_ID";

    @Override
    protected final Fragment createFragment() {
        final HelpNode node = getIntent().getParcelableExtra(EXTRA_HELP_NODE);
        final String bookingId = getIntent().getStringExtra(EXTRA_BOOKING_ID);
        return HelpFragment.newInstance(node, bookingId);
    }

    @Override
    protected final String getNavItemTitle() {
        return getString(R.string.help);
    }
}
