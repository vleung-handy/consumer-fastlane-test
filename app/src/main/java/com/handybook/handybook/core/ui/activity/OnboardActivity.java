package com.handybook.handybook.core.ui.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;

import com.handybook.handybook.core.ui.fragment.OnboardFragment;
import com.handybook.handybook.onboarding.OnboardV2Fragment;

public final class OnboardActivity extends MenuDrawerActivity {
    private static final String TAG = "OnboardActivity";

    Fragment mFragment;

    @Override
    protected final Fragment createFragment() {
        if (mConfiguration == null)
        {
            mConfiguration = mConfigurationManager.getCachedConfiguration();

            if (mConfiguration == null)
            {
                Log.d(TAG, "createFragment: mConfiguration is null");
            }
        }

        if (mConfiguration != null && mConfiguration.isOnboardingEnabled())
        {
            mFragment = OnboardV2Fragment.newInstance();
            return mFragment;
        }

        Log.d(TAG, "createFragment: returning legacy onboarding fragment");
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
}
