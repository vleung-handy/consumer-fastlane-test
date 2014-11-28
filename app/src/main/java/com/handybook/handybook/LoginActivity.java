package com.handybook.handybook;


import android.content.Intent;
import android.support.v4.app.Fragment;

public final class LoginActivity extends MenuDrawerActivity {
    static final String EXTRA_FIND_USER = "com.handy.handy.EXTRA_FIND_USER";
    static final String EXTRA_BOOKING_USER_NAME = "com.handy.handy.EXTRA_BOOKING_USER_NAME";
    static final String EXTRA_BOOKING_EMAIL = "com.handy.handy.EXTRA_BOOKING_EMAIL";
    static final int RESULT_FINISH = 1;

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
        if (resultCode == RESULT_FINISH) finish();
    }

    @Override
    protected final String getNavItemTitle() {
        return getString(R.string.log_in);
    }
}
