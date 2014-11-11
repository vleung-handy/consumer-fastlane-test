package com.handybook.handybook;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import java.util.ArrayList;
import java.util.HashMap;

public final class BookingOptionsActivity extends MenuDrawerActivity {
    static final String EXTRA_OPTIONS = "com.handy.handy.EXTRA_OPTIONS";
    static final String EXTRA_POST_OPTIONS = "com.handy.handy.EXTRA_POST_OPTIONS";
    static final String EXTRA_CHILD_DISPLAY_MAP = "com.handy.handy.EXTRA_CHILD_DISPLAY_MAP";
    static final String EXTRA_PAGE = "com.handy.handy.EXTRA_PAGE";

    @Override
    protected final Fragment createFragment() {
        final int page = getIntent().getIntExtra(EXTRA_PAGE, 0);
        final ArrayList<BookingOption> options = getIntent().getParcelableArrayListExtra(EXTRA_OPTIONS);

        final ArrayList<BookingOption> postOptions
                = getIntent().getParcelableArrayListExtra(EXTRA_POST_OPTIONS);

        final HashMap<String, Boolean> childDisplayMap
                = (HashMap)getIntent().getSerializableExtra(EXTRA_CHILD_DISPLAY_MAP);

        return BookingOptionsFragment.newInstance(options, page, childDisplayMap, postOptions);
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
