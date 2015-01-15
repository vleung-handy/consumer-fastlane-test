package com.handybook.handybook.ui.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.handybook.handybook.ui.fragment.OnboardFragment;

public final class OnboardActivity extends MenuDrawerActivity {

    @Override
    protected final Fragment createFragment() {
        return OnboardFragment.newInstance();
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
