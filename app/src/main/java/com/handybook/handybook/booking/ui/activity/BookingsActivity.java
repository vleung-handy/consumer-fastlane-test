package com.handybook.handybook.booking.ui.activity;

import android.content.Intent;
import android.support.v4.app.Fragment;

import com.handybook.handybook.R;
import com.handybook.handybook.booking.ui.fragment.BookingsFragment;
import com.handybook.handybook.ui.activity.MenuDrawerActivity;

public final class BookingsActivity extends MenuDrawerActivity
{
    @Override
    protected boolean requiresUser()
    {
        return true;
    }

    @Override
    protected final Fragment createFragment() {
        return BookingsFragment.newInstance();
    }

    @Override
    protected final String getNavItemTitle() {
        return getString(R.string.my_bookings);
    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        final Fragment fragment =
                getSupportFragmentManager().findFragmentById(R.id.fragment_container);
        if (fragment != null)
        {
            fragment.onActivityResult(requestCode, resultCode, data);
        }
    }
}
