package com.handybook.handybook;

import android.support.v4.app.Fragment;

public final class PromosActivity extends MenuDrawerActivity {

    @Override
    protected final Fragment createFragment() {
        return PromosFragment.newInstance();
    }

    @Override
    protected final String getNavItemTitle() {
        return getString(R.string.promotions);
    }
}
