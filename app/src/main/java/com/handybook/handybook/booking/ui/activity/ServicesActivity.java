package com.handybook.handybook.booking.ui.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.handybook.handybook.booking.model.Service;
import com.handybook.handybook.booking.ui.fragment.ServicesFragment;
import com.handybook.handybook.ui.activity.MenuDrawerActivity;

public final class ServicesActivity extends MenuDrawerActivity
{
    public static final String EXTRA_SERVICE = "com.handy.handy.EXTRA_SERVICE";

    @Override
    protected final Fragment createFragment() {
        final Service service = getIntent().getParcelableExtra(EXTRA_SERVICE);
        return ServicesFragment.newInstance(service);
    }

    @Override
    protected final String getNavItemTitle() {
        return null;
    }

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        disableDrawer = true;
    }
}
