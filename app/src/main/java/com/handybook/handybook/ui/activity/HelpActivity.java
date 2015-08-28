package com.handybook.handybook.ui.activity;

import android.support.v4.app.Fragment;

import com.handybook.handybook.R;
import com.handybook.handybook.constant.BundleKeys;
import com.handybook.handybook.core.HelpNode;
import com.handybook.handybook.ui.fragment.HelpFragment;

public final class HelpActivity extends MenuDrawerActivity
{
    @Override
    protected final Fragment createFragment()
    {
        final HelpNode associatedNode = getIntent().getParcelableExtra(BundleKeys.HELP_NODE);
        final String nodeId = getIntent().getStringExtra(BundleKeys.HELP_NODE_ID);
        final String bookingId = getIntent().getStringExtra(BundleKeys.BOOKING_ID);
        final String loginToken = getIntent().getStringExtra(BundleKeys.LOGIN_TOKEN);
        final String path = getIntent().getStringExtra(BundleKeys.PATH);
        final boolean nodeIsBooking = getIntent().getBooleanExtra(BundleKeys.HELP_NODE_IS_BOOKING, false);
        return HelpFragment.newInstance(bookingId, loginToken, path, associatedNode, nodeId, nodeIsBooking);
    }

    @Override
    protected final String getNavItemTitle()
    {
        return getString(R.string.help);
    }
}
