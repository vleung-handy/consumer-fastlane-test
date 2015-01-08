package com.handybook.handybook.ui.activity;

import android.support.v4.app.Fragment;

import com.handybook.handybook.R;
import com.handybook.handybook.ui.fragment.PromosFragment;

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
