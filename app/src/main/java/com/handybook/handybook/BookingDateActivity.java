package com.handybook.handybook;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import java.util.ArrayList;

public final class BookingDateActivity extends MenuDrawerActivity {
    static final String EXTRA_POST_OPTIONS = "com.handy.handy.EXTRA_POST_OPTIONS";

    @Override
    protected final Fragment createFragment() {
        final ArrayList<BookingOption> postOptions
                = getIntent().getParcelableArrayListExtra(EXTRA_POST_OPTIONS);
        return BookingDateFragment.newInstance(postOptions);
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
