package com.handybook.handybook;

import android.os.Bundle;
import android.support.v4.app.Fragment;

public final class ServicesActivity extends MenuDrawerActivity {
    static final String EXTRA_SERVICE = "com.handy.handy.EXTRA_SERVICE";
    static final String EXTRA_NAV_HEIGHT = "com.handy.handy.EXTRA_NAV_HEIGHT";

    @Override
    protected final Fragment createFragment() {
        final Service service = getIntent().getParcelableExtra(EXTRA_SERVICE);
        final int height = getIntent().getIntExtra(EXTRA_NAV_HEIGHT, 0);
        return ServicesFragment.newInstance(service, height);
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
