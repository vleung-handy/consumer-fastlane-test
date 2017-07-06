package com.handybook.handybook.onboarding;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import com.handybook.handybook.R;
import com.handybook.handybook.core.constant.ActivityResult;
import com.handybook.handybook.core.ui.activity.SingleFragmentActivity;

public final class OnboardActivity extends SingleFragmentActivity {

    @Override
    protected final Fragment createFragment() {
        if (mConfigurationManager.getPersistentConfiguration().isSaveZipCodeEnabled()) {
            return OnboardV2Fragment.newInstance();
        }
        else {
            return OnboardFragment.newInstance();
        }
    }

    @Override
    public void onBackPressed() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment fragment = fragmentManager.findFragmentById(R.id.fragment_container);

        if (fragment != null && fragment instanceof OnboardV2Fragment) {
            if (((OnboardV2Fragment) fragment).onBackPressed()) {
                return;
            }
        }

        super.onBackPressed();
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
