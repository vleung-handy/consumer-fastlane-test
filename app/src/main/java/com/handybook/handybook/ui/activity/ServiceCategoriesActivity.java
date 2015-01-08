package com.handybook.handybook.ui.activity;

import android.support.v4.app.Fragment;

import com.handybook.handybook.R;
import com.handybook.handybook.ui.fragment.ServiceCategoriesFragment;

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
