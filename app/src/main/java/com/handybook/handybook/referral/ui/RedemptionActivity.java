package com.handybook.handybook.referral.ui;

import android.content.Intent;
import android.support.v4.app.Fragment;

import com.handybook.handybook.booking.ui.activity.ServiceCategoriesActivity;
import com.handybook.handybook.core.ui.activity.MenuDrawerActivity;
import com.handybook.handybook.referral.util.ReferralIntentUtil;

public class RedemptionActivity extends MenuDrawerActivity {

    @Override
    protected Fragment createFragment() {
        String referralGuid = ReferralIntentUtil.getReferralGuidFromIntent(getIntent());
        if (referralGuid == null || mUserManager.isUserLoggedIn()) // new users only
        {
            navigateToHomeScreen();
            finish();
        }
        return com.handybook.handybook.referral.ui.RedemptionFragment.newInstance(
                referralGuid);
    }

    @Override
    protected String getNavItemTitle() {
        return null;
    }

    private void navigateToHomeScreen() {
        final Intent intent = new Intent(this, ServiceCategoriesActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK
                        | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }
}
