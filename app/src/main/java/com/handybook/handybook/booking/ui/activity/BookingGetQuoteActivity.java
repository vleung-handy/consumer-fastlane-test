package com.handybook.handybook.booking.ui.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.handybook.handybook.booking.model.BookingOption;
import com.handybook.handybook.booking.ui.fragment.BookingGetQuoteFragment;
import com.handybook.handybook.core.ui.activity.MenuDrawerActivity;

import java.util.ArrayList;
import java.util.HashMap;

import static com.handybook.handybook.booking.ui.fragment.BookingOptionsInputFragment.EXTRA_CHILD_DISPLAY_MAP;
import static com.handybook.handybook.booking.ui.fragment.BookingOptionsInputFragment.EXTRA_OPTIONS;

public final class BookingGetQuoteActivity extends MenuDrawerActivity {

    /**
     * mostly copied from BookingOptionsActivity
     */

    @Override
    protected final Fragment createFragment() {
        final ArrayList<BookingOption> options =
                getIntent().getParcelableArrayListExtra(EXTRA_OPTIONS);

        final HashMap<String, Boolean> childDisplayMap
                = (HashMap) getIntent().getSerializableExtra(EXTRA_CHILD_DISPLAY_MAP);

        return BookingGetQuoteFragment.newInstance(
                options, childDisplayMap, getIntent().getExtras());
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
