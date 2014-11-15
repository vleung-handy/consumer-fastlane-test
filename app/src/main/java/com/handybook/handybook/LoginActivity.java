package com.handybook.handybook;


import android.support.v4.app.Fragment;

public final class LoginActivity extends MenuDrawerActivity {
    static final String EXTRA_IS_FOR_BOOKING = "com.handy.handy.EXTRA_IS_FOR_BOOKING";

    @Override
    protected final Fragment createFragment() {
        final boolean isForBooking = getIntent().getBooleanExtra(EXTRA_IS_FOR_BOOKING, false);
        return LoginFragment.newInstance(isForBooking);
    }

    @Override
    protected final String getNavItemTitle() {
        return getString(R.string.log_in);
    }
}
