package com.handybook.handybook.ui.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.handybook.handybook.constant.BundleKeys;
import com.handybook.handybook.core.Booking;
import com.handybook.handybook.ui.fragment.BookingEditExtrasFragment;

public final class BookingEditExtrasActivity extends MenuDrawerActivity
{

    //TODO: can we consolidate this with other edit activities?
    @Override
    protected final Fragment createFragment()
    {
        final Booking booking = getIntent().getParcelableExtra(BundleKeys.BOOKING);
        return BookingEditExtrasFragment.newInstance(booking);
    }

    @Override
    protected final String getNavItemTitle()
    {
        return null;
    }

    @Override
    protected void onCreate(final Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        disableDrawer = true;
    }
}
