package com.handybook.handybook;

import android.support.v4.app.Fragment;

public final class ServiceCategoriesActivity extends MenuDrawerActivity {
    @Override
    protected final Fragment createFragment() {
        return ServiceCategoriesFragment.newInstance();
    }

    @Override
    protected final String getNavItemTitle() {
        return getString(R.string.home);
    }
}
