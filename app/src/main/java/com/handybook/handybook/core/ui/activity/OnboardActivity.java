package com.handybook.handybook.core.ui.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.handybook.handybook.core.ui.fragment.OnboardFragment;
import com.handybook.handybook.onboarding.OnboardV2Fragment;

public final class OnboardActivity extends MenuDrawerActivity {

    Fragment mFragment;

    @Override
    protected final Fragment createFragment() {
        //TODO: JIA: remove this hard coding
        if (1 == 1)
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
}
