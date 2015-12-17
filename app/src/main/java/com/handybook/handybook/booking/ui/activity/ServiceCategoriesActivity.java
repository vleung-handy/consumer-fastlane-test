package com.handybook.handybook.booking.ui.activity;

import android.support.v4.app.Fragment;

import com.handybook.handybook.R;
import com.handybook.handybook.booking.ui.fragment.ServiceCategoriesFragment;
import com.handybook.handybook.ui.activity.MenuDrawerActivity;

public final class ServiceCategoriesActivity extends MenuDrawerActivity
{
    @Override
    protected final Fragment createFragment() {
        return ServiceCategoriesFragment.newInstance();
    }

    @Override
    protected final String getNavItemTitle() {
        return getString(R.string.home);
    }
}
