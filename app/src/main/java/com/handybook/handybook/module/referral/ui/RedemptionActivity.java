package com.handybook.handybook.module.referral.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.handybook.handybook.booking.ui.activity.ServiceCategoriesActivity;
import com.handybook.handybook.module.referral.util.ReferralIntentUtil;
import com.handybook.handybook.core.ui.activity.MenuDrawerActivity;

public class RedemptionActivity extends MenuDrawerActivity
{

    @Override
    protected void onCreate(final Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setDrawerDisabled(true);
    }

    @Override
    protected Fragment createFragment()
    {
        String referralGuid = ReferralIntentUtil.getReferralGuidFromIntent(getIntent());
        if (referralGuid == null || mUserManager.isUserLoggedIn()) // new users only
        {
            navigateToHomeScreen();
            finish();
        }
        return RedemptionFragment.newInstance(referralGuid);
    }

    @Override
    protected String getNavItemTitle()
    {
        return null;
    }

    private void navigateToHomeScreen()
    {
        final Intent intent = new Intent(this, ServiceCategoriesActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK
                | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }
}
