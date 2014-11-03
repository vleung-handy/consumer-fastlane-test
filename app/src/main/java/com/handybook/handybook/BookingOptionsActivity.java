package com.handybook.handybook;

import android.os.Bundle;
import android.support.v4.app.Fragment;

public final class BookingOptionsActivity extends MenuDrawerActivity {
    @Override
    protected final Fragment createFragment() {
        return BookingOptionsFragment.newInstance();
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
