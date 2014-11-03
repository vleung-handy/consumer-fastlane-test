package com.handybook.handybook;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import java.util.ArrayList;
import java.util.HashMap;

public final class BookingOptionsActivity extends MenuDrawerActivity {
    static final String EXTRA_OPTIONS = "com.handy.handy.EXTRA_OPTIONS";
    static final String EXTRA_CHILD_DISPLAY_MAP = "com.handy.handy.EXTRA_CHILD_DISPLAY_MAP";
    static final String EXTRA_PAGE = "com.handy.handy.EXTRA_PAGE";

    @Override
    protected final Fragment createFragment() {
        final ArrayList<BookingOption> options = getIntent().getParcelableArrayListExtra(EXTRA_OPTIONS);
        final int page = getIntent().getIntExtra(EXTRA_PAGE, 0);

        final HashMap<String, Boolean> childDisplayMap
                = (HashMap)getIntent().getSerializableExtra(EXTRA_CHILD_DISPLAY_MAP);

        if (childDisplayMap != null) return BookingOptionsFragment
                .newInstance(options, page, childDisplayMap);

        return BookingOptionsFragment.newInstance(options, page);
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
