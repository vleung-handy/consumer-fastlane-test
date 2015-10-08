package com.handybook.handybook.ui.activity;


import android.content.Intent;
import android.support.v4.app.Fragment;

import com.handybook.handybook.R;
import com.handybook.handybook.constant.ActivityResult;
import com.handybook.handybook.ui.fragment.LoginFragment;

public final class LoginActivity extends MenuDrawerActivity {
    public static final String EXTRA_FIND_USER = "com.handy.handy.EXTRA_FIND_USER";
    public static final String EXTRA_BOOKING_USER_NAME = "com.handy.handy.EXTRA_BOOKING_USER_NAME";
    public static final String EXTRA_BOOKING_EMAIL = "com.handy.handy.EXTRA_BOOKING_EMAIL";


    @Override
    protected final Fragment createFragment() {
        final boolean findUser = getIntent().getBooleanExtra(EXTRA_FIND_USER, false);
        final String userName = getIntent().getStringExtra(EXTRA_BOOKING_USER_NAME);
        final String userEmail = getIntent().getStringExtra(EXTRA_BOOKING_EMAIL);
        return LoginFragment.newInstance(findUser, userName, userEmail);
    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode,
                                    final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == ActivityResult.RESULT_LOGIN_FINISH) finish();
    }

    @Override
    protected final String getNavItemTitle() {
        return getString(R.string.log_in);
    }
}
