package com.handybook.handybook;

import android.app.Fragment;

public class LoginActivity extends MenuDrawerActivity {
    @Override
    protected final Fragment createFragment() {
        return LoginActivityFragment.newInstance();
    }

    @Override
    protected final String getNavItemTitle() {
        return getString(R.string.log_in);
    }
}
