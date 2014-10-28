package com.handybook.handybook;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import java.util.ArrayList;

public final class ServicesActivity extends MenuDrawerActivity {
    static final String EXTRA_SERVICES = "com.handy.handy.EXTRA_SERVICES";

    @Override
    protected final Fragment createFragment() {
        final ArrayList<Service> services = getIntent().getParcelableArrayListExtra(EXTRA_SERVICES);
        return ServicesFragment.newInstance(services);
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
