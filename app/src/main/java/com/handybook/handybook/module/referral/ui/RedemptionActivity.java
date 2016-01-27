package com.handybook.handybook.module.referral.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.handybook.handybook.booking.ui.activity.ServiceCategoriesActivity;
import com.handybook.handybook.module.referral.util.ReferralIntentUtil;
import com.handybook.handybook.ui.activity.MenuDrawerActivity;

public class RedemptionActivity extends MenuDrawerActivity
{
    private String mReferralGuid;

    @Override
    protected void onCreate(final Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setDrawerDisabled(true);
        mReferralGuid = ReferralIntentUtil.getReferralGuidFromIntent(getIntent());
        if (mReferralGuid == null || mUserManager.isUserLoggedIn()) // new users only
        {
            navigateToHomeScreen();
        }
    }

    @Override
    protected Fragment createFragment()
    {
        return RedemptionFragment.newInstance(mReferralGuid);
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
        finish();
    }
}
