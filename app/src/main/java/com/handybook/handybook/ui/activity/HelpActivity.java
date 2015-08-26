package com.handybook.handybook.ui.activity;

import android.support.v4.app.Fragment;

import com.handybook.handybook.R;
import com.handybook.handybook.constant.BundleKeys;
import com.handybook.handybook.ui.fragment.HelpFragment;

public final class HelpActivity extends MenuDrawerActivity {
    public static final String EXTRA_HELP_NODE = "com.handy.handy.EXTRA_HELP_NODE";
    public static final String EXTRA_BOOKING_ID = "com.handy.handy.EXTRA_BOOKING_ID";
    public static final String EXTRA_LOGIN_TOKEN = "com.handy.handy.EXTRA_LOGIN_TOKEN";
    public static final String EXTRA_PATH = "com.handy.handy.EXTRA_PATH";

    @Override
    protected final Fragment createFragment() {
        //final HelpNode node = getIntent().getParcelableExtra(EXTRA_HELP_NODE);

        final String nodeId = getIntent().getStringExtra(BundleKeys.HELP_NODE_ID);
        final String bookingId = getIntent().getStringExtra(EXTRA_BOOKING_ID);
        final String loginToken = getIntent().getStringExtra(EXTRA_LOGIN_TOKEN);
        final String path = getIntent().getStringExtra(EXTRA_PATH);
        return HelpFragment.newInstance(bookingId, loginToken, path, nodeId);
    }

    @Override
    protected final String getNavItemTitle() {
        return getString(R.string.help);
    }
}
