package com.handybook.handybook;


import android.support.v4.app.Fragment;

public final class LoginActivity extends MenuDrawerActivity {
    static final String EXTRA_IS_FOR_BOOKING = "com.handy.handy.EXTRA_IS_FOR_BOOKING";
    static final String EXTRA_USER_NAME = "com.handy.handy.EXTRA_USER_NAME";
    static final String EXTRA_USER_EMAIL = "com.handy.handy.EXTRA_USER_EMAIL";

    @Override
    protected final Fragment createFragment() {
        final boolean isForBooking = getIntent().getBooleanExtra(EXTRA_IS_FOR_BOOKING, false);
        final String userName = getIntent().getStringExtra(EXTRA_USER_NAME);
        final String userEmail = getIntent().getStringExtra(EXTRA_USER_EMAIL);
        return LoginFragment.newInstance(isForBooking, userName, userEmail);
    }

    @Override
    protected final String getNavItemTitle() {
        return getString(R.string.log_in);
    }
}
