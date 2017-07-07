package com.handybook.handybook.booking.ui.activity;

import android.support.v4.app.Fragment;

import com.handybook.handybook.booking.model.BookingOption;
import com.handybook.handybook.booking.ui.fragment.BookingOptionsFragment;
import com.handybook.handybook.core.ui.activity.SingleFragmentActivity;

import java.util.ArrayList;
import java.util.HashMap;

public final class BookingOptionsActivity extends SingleFragmentActivity {

    public static final String EXTRA_OPTIONS = "com.handy.handy.EXTRA_OPTIONS";
    public static final String EXTRA_POST_OPTIONS = "com.handy.handy.EXTRA_POST_OPTIONS";
    public static final String EXTRA_CHILD_DISPLAY_MAP = "com.handy.handy.EXTRA_CHILD_DISPLAY_MAP";
    public static final String EXTRA_PAGE = "com.handy.handy.EXTRA_PAGE";
    public static final String EXTRA_IS_POST = "com.handy.handy.EXTRA_IS_POST";

    @Override
    protected final Fragment createFragment() {
        final int page = getIntent().getIntExtra(EXTRA_PAGE, 0);
        final ArrayList<BookingOption> options = getIntent().getParcelableArrayListExtra(
                EXTRA_OPTIONS);

        final ArrayList<BookingOption> postOptions
                = getIntent().getParcelableArrayListExtra(EXTRA_POST_OPTIONS);

        final HashMap<String, Boolean> childDisplayMap
                = (HashMap) getIntent().getSerializableExtra(EXTRA_CHILD_DISPLAY_MAP);

        final boolean isPost = getIntent().getBooleanExtra(EXTRA_IS_POST, false);

        return BookingOptionsFragment.newInstance(
                options,
                page,
                childDisplayMap,
                postOptions,
                isPost,
                getIntent().getExtras()
        );
    }
}
