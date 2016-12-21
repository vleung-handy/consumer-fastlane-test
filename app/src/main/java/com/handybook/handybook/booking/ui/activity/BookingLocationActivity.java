package com.handybook.handybook.booking.ui.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.handybook.handybook.booking.ui.fragment.BookingLocationFragment;
import com.handybook.handybook.core.ui.activity.MenuDrawerActivity;

public final class BookingLocationActivity extends MenuDrawerActivity
{

    @Override
    protected final Fragment createFragment() {
        return BookingLocationFragment.newInstance();
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
