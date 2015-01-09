package com.handybook.handybook.ui.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.widget.ListView;

import com.handybook.handybook.ui.fragment.BookingCancelOptionsFragment;

import java.util.ArrayList;
import java.util.List;

public final class BookingCancelOptionsActivity extends MenuDrawerActivity {
    public static final String EXTRA_OPTIONS = "com.handy.handy.EXTRA_OPTIONS";
    public static final String EXTRA_NOTICE = "com.handy.handy.EXTRA_NOTICE";

    @Override
    protected final Fragment createFragment() {
        final String notice = getIntent().getStringExtra(EXTRA_NOTICE);
        final ArrayList<String> options = getIntent().getStringArrayListExtra(EXTRA_OPTIONS);
        return BookingCancelOptionsFragment.newInstance(notice, options);
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
