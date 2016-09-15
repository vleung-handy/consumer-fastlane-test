package com.handybook.handybook.booking.ui.activity;

import android.content.Intent;
import android.support.v4.app.Fragment;

import com.handybook.handybook.R;
import com.handybook.handybook.booking.ui.fragment.BookingsFragment;
import com.handybook.handybook.booking.ui.view.ServiceCategoriesOverlayFragment;
import com.handybook.handybook.module.bookings.UpcomingBookingsFragment;
import com.handybook.handybook.module.configuration.model.Configuration;
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

        Configuration config = mConfigurationManager.getPersistentConfiguration();
        if (config != null && config.isUpcomingAndPastBookingsEnabled())
        {
            return UpcomingBookingsFragment.newInstance();
        }
        else
        {
            return BookingsFragment.newInstance();
        }

    }

    @Override
    public void onBackPressed()
    {
        //if the over lay fragment is showing, dismiss it and swallow the back press
        ServiceCategoriesOverlayFragment frag = (ServiceCategoriesOverlayFragment) getSupportFragmentManager()
                .findFragmentByTag(BookingsFragment.mOverlayFragmentTag);
        if (frag != null)
        {
            frag.animateAndDismissFragment();
        }
        else
        {
            super.onBackPressed();
        }
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
