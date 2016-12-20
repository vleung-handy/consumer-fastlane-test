package com.handybook.handybook.booking.ui.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.handybook.handybook.booking.ui.fragment.BookingExtrasFragment;
import com.handybook.handybook.core.ui.activity.MenuDrawerActivity;

public final class BookingExtrasActivity extends MenuDrawerActivity
{

    @Override
    protected final Fragment createFragment() {
        return BookingExtrasFragment.newInstance();
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
