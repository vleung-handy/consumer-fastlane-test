package com.handybook.handybook.referral.ui;

import android.support.v4.app.Fragment;

import com.handybook.handybook.core.constant.BundleKeys;
import com.handybook.handybook.core.ui.activity.SingleFragmentActivity;

public class ReferralActivity extends SingleFragmentActivity {

    @Override
    protected Fragment createFragment() {
        return ReferralFragment.newInstance(getIntent().getStringExtra(BundleKeys.REFERRAL_PAGE_SOURCE));
    }
}
