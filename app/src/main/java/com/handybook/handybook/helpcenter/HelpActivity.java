package com.handybook.handybook.helpcenter;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;

import com.handybook.handybook.R;
import com.handybook.handybook.constant.BundleKeys;
import com.handybook.handybook.ui.activity.MenuDrawerActivity;

public final class HelpActivity extends MenuDrawerActivity
{
    public static final int HELP_NODE_ID_CANCEL = 296;
    public static final int HELP_NODE_ID_PRO_LATE = 450;
    public static final int HELP_NODE_ID_ADJUST_HOURS = 498;

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

    public static Intent getIntentToOpenNodeId(final Context context, final int helpNodeId)
    {
        final Intent intent = new Intent(context, HelpActivity.class);
        intent.putExtra(BundleKeys.HELP_NODE_ID, Integer.toString(helpNodeId));
        return intent;
    }

    @Override
    protected final String getNavItemTitle()
    {
        return getString(R.string.help);
    }
}
