package com.handybook.handybook.helpcenter.helpcontact.ui.activity;

import android.support.v4.app.Fragment;

import com.handybook.handybook.core.constant.BundleKeys;
import com.handybook.handybook.helpcenter.helpcontact.ui.fragment.HelpContactFragment;
import com.handybook.handybook.helpcenter.model.HelpNode;
import com.handybook.handybook.core.ui.activity.MenuDrawerActivity;

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
