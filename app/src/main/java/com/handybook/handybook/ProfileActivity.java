package com.handybook.handybook;


import android.support.v4.app.Fragment;

public final class ProfileActivity extends MenuDrawerActivity {
    @Override
    protected final Fragment createFragment() {
        return ProfileFragment.newInstance();
    }

    @Override
    protected final String getNavItemTitle() {
        return getString(R.string.profile);
    }
}
