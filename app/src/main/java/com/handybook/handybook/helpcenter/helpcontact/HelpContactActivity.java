package com.handybook.handybook.helpcenter.helpcontact;

import android.support.v4.app.Fragment;

import com.handybook.handybook.constant.BundleKeys;
import com.handybook.handybook.helpcenter.HelpNode;
import com.handybook.handybook.ui.activity.MenuDrawerActivity;

public final class HelpContactActivity extends MenuDrawerActivity {

    @Override
    protected final Fragment createFragment() {
        final HelpNode node = getIntent().getParcelableExtra(BundleKeys.HELP_NODE);
        final String path = getIntent().getStringExtra(BundleKeys.PATH);
        final String bookingId = getIntent().getStringExtra(BundleKeys.BOOKING_ID);
        return HelpContactFragment.newInstance(node, path, bookingId);
    }

    @Override
    protected final String getNavItemTitle() {
        return "";
    }
}
