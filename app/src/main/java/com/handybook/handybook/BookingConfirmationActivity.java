package com.handybook.handybook;

import android.os.Bundle;
import android.support.v4.app.Fragment;

public final class BookingConfirmationActivity extends MenuDrawerActivity {
    static final String EXTRA_IS_LAST = "com.handy.handy.EXTRA_IS_LAST";

    @Override
    protected final Fragment createFragment() {
        final boolean isLast = getIntent().getBooleanExtra(EXTRA_IS_LAST, false);
        return BookingConfirmationFragment.newInstance(isLast);
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
