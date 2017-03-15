package com.handybook.handybook.referral.ui;

import android.support.v4.app.Fragment;

import com.handybook.handybook.R;
import com.handybook.handybook.core.constant.BundleKeys;
import com.handybook.handybook.core.ui.activity.MenuDrawerActivity;

public class ReferralActivity extends MenuDrawerActivity {

    @Override
    protected boolean requiresUser() {
        return true;
    }

    @Override
    protected Fragment createFragment() {
        return ReferralFragment.newInstance(
                null,
                getIntent().getStringExtra(BundleKeys.REFERRAL_PAGE_SOURCE),
                false
        );
    }

    @Override
    protected String getNavItemTitle() {
        return getString(R.string.free_cleanings);
    }
}
