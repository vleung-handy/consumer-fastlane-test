package com.handybook.handybook.onboarding;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.handybook.handybook.core.constant.ActivityResult;
import com.handybook.handybook.core.ui.activity.MenuDrawerActivity;

public final class OnboardActivity extends MenuDrawerActivity {

    @Override
    protected final Fragment createFragment() {
        if (mConfigurationManager.getPersistentConfiguration().isSaveZipCodeEnabled()) {
            setActiveFragment(OnboardV2Fragment.newInstance());
        }
        else {
            setActiveFragment(OnboardFragment.newInstance());
        }

        return getActiveFragemnt();
    }

    @Override
    public void onBackPressed() {
        if (getActiveFragemnt() != null && getActiveFragemnt() instanceof OnboardV2Fragment) {
            if (((OnboardV2Fragment) getActiveFragemnt()).onBackPressed()) {
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

    @Override
    protected void onActivityResult(
            final int requestCode,
            final int resultCode,
            final Intent data
    ) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == ActivityResult.LOGIN_FINISH) {
            //Will reach here if a login request originated from this activity is completed.
            finish();
        }
    }
}
