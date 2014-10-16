package com.handybook.handybook;


import android.support.v4.app.Fragment;

public final class LoginActivity extends MenuDrawerActivity {
    @Override
    protected final Fragment createFragment() {
        return LoginFragment.newInstance();
    }

    @Override
    protected final String getNavItemTitle() {
        return getString(R.string.log_in);
    }
}
