package com.handybook.handybook.core.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.handybook.handybook.core.ui.fragment.OnboardFragment;
import com.handybook.handybook.onboarding.OnboardV2Fragment;

import static com.handybook.handybook.core.constant.ActivityResult.LOGIN_FINISH;

public final class OnboardActivity extends MenuDrawerActivity {

    Fragment mFragment;

    @Override
    protected final Fragment createFragment() {
        if (mConfigurationManager.getPersistentConfiguration().isOnboardingEnabled())
        {
            mFragment = OnboardV2Fragment.newInstance();
            return mFragment;
        }

        return OnboardFragment.newInstance();
    }

    @Override
    public void onBackPressed()
    {
        if (mFragment != null && mFragment instanceof OnboardV2Fragment)
        {
            if (((OnboardV2Fragment) mFragment).onBackPressed())
            {
                return;
            }
        }

        super.onBackPressed();
    }

    @Override
    protected final String getNavItemTitle() {
        return null;
    }

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        disableDrawer = true;
    }

    public Fragment getActiveFragment()
    {
        return mFragment;
    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == LOGIN_FINISH)
        {
            //Will reach here if a login request originated from this activity is completed.
            finish();
        }
    }
}
