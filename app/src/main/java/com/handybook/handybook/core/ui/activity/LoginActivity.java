package com.handybook.handybook.core.ui.activity;


import android.content.Intent;
import android.support.v4.app.Fragment;

import com.handybook.handybook.R;
import com.handybook.handybook.core.constant.ActivityResult;
import com.handybook.handybook.core.ui.fragment.LoginFragment;

public final class LoginActivity extends MenuDrawerActivity {
    public static final String EXTRA_FROM_BOOKING_FUNNEL = "com.handy.handy.EXTRA_FROM_BOOKING_FUNNEL";
    public static final String EXTRA_FIND_USER = "com.handy.handy.EXTRA_FIND_USER";
    public static final String EXTRA_BOOKING_USER_NAME = "com.handy.handy.EXTRA_BOOKING_USER_NAME";
    public static final String EXTRA_BOOKING_EMAIL = "com.handy.handy.EXTRA_BOOKING_EMAIL";
    public static final String EXTRA_FROM_ONBOARDING = "com.handy.handy.EXTRA_FROM_ONBOARDING";


    @Override
    protected final Fragment createFragment() {
        final boolean findUser = getIntent().getBooleanExtra(EXTRA_FIND_USER, false);
        final String userName = getIntent().getStringExtra(EXTRA_BOOKING_USER_NAME);
        final String userEmail = getIntent().getStringExtra(EXTRA_BOOKING_EMAIL);
        final boolean fromBookingFunnel = getIntent().getBooleanExtra(EXTRA_FROM_BOOKING_FUNNEL, false);
        final boolean fromOnboarding = getIntent().getBooleanExtra(EXTRA_FROM_ONBOARDING, false);
        return LoginFragment.newInstance(
                findUser,
                userName,
                userEmail,
                fromBookingFunnel,
                fromOnboarding
        );
    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode,
                                    final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == ActivityResult.LOGIN_FINISH) finish();
    }

    @Override
    protected final String getNavItemTitle() {
        return getString(R.string.sign_in);
    }
}
