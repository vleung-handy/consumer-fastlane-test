package com.handybook.handybook.booking.ui.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.handybook.handybook.booking.model.BookingOption;
import com.handybook.handybook.booking.ui.fragment.BookingGetQuoteFragment;
import com.handybook.handybook.core.ui.activity.MenuDrawerActivity;

import java.util.ArrayList;
import java.util.HashMap;

import static com.handybook.handybook.booking.ui.fragment.BookingOptionsInputFragment.EXTRA_CHILD_DISPLAY_MAP;
import static com.handybook.handybook.booking.ui.fragment.BookingOptionsInputFragment.EXTRA_IS_POST;
import static com.handybook.handybook.booking.ui.fragment.BookingOptionsInputFragment.EXTRA_OPTIONS;
import static com.handybook.handybook.booking.ui.fragment.BookingOptionsInputFragment.EXTRA_PAGE;
import static com.handybook.handybook.booking.ui.fragment.BookingOptionsInputFragment.EXTRA_POST_OPTIONS;

public final class BookingGetQuoteActivity extends MenuDrawerActivity
{

    /**
     * mostly copied from BookingOptionsActivity
     */

    @Override
    protected final Fragment createFragment() {
        final int page = getIntent().getIntExtra(EXTRA_PAGE, 0);
        final ArrayList<BookingOption> options = getIntent().getParcelableArrayListExtra(EXTRA_OPTIONS);

        final ArrayList<BookingOption> postOptions
                = getIntent().getParcelableArrayListExtra(EXTRA_POST_OPTIONS);

        final HashMap<String, Boolean> childDisplayMap
                = (HashMap)getIntent().getSerializableExtra(EXTRA_CHILD_DISPLAY_MAP);

        final boolean isPost = getIntent().getBooleanExtra(EXTRA_IS_POST, false);

        return BookingGetQuoteFragment.newInstance(options, page, childDisplayMap, postOptions, isPost);
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
