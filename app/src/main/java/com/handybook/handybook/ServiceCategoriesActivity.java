package com.handybook.handybook;

import android.app.Fragment;

public final class ServiceCategoriesActivity extends MenuDrawerActivity {
    @Override
    protected final Fragment createFragment() {
        return ServiceCategoriesFragment.newInstance();
    }
}
