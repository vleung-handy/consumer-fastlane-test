package com.handybook.handybook;

import android.os.Bundle;
import android.support.v4.app.Fragment;

public final class PeakPricingActivity extends MenuDrawerActivity {

    @Override
    protected final Fragment createFragment() {
        return PeakPricingFragment.newInstance();
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