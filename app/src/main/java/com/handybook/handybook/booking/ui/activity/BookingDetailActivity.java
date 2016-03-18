package com.handybook.handybook.booking.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.handybook.handybook.booking.model.Booking;
import com.handybook.handybook.booking.ui.fragment.BookingDetailFragment;
import com.handybook.handybook.constant.BundleKeys;
import com.handybook.handybook.ui.activity.MenuDrawerActivity;

public final class BookingDetailActivity extends MenuDrawerActivity
{

    @Override
    protected final Fragment createFragment()
    {
        final Booking booking = getIntent().getParcelableExtra(BundleKeys.BOOKING);
        if (booking != null)
        {
            return BookingDetailFragment.newInstance(booking);
        }
        else
        {
            final String bookingId = getIntent().getStringExtra(BundleKeys.BOOKING_ID);
            return BookingDetailFragment.newInstance(bookingId);
        }
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

    //Always return to mybookings page even if you came from somewhere else
    @Override
    public void onBackPressed()
    {
        final Intent intent = new Intent(getApplicationContext(), BookingsActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }
}
