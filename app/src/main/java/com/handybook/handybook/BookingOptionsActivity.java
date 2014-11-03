package com.handybook.handybook;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import java.util.ArrayList;

public final class BookingOptionsActivity extends MenuDrawerActivity {
    static final String EXTRA_OPTIONS = "com.handy.handy.EXTRA_OPTIONS";

    @Override
    protected final Fragment createFragment() {
        final ArrayList<BookingOption> options = getIntent().getParcelableArrayListExtra(EXTRA_OPTIONS);
        return BookingOptionsFragment.newInstance(options);
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
