package com.handybook.handybook.booking.ui.activity;

import android.content.Intent;
import android.support.v4.app.Fragment;

import com.handybook.handybook.R;
import com.handybook.handybook.booking.ui.fragment.UpcomingAndPastBookingsFragment;
import com.handybook.handybook.booking.ui.fragment.UpcomingBookingsFragment;
import com.handybook.handybook.booking.ui.view.ServiceCategoriesOverlayFragment;
import com.handybook.handybook.configuration.manager.ConfigurationManager;
import com.handybook.handybook.core.ui.activity.SingleFragmentActivity;

import javax.inject.Inject;

public final class BookingsActivity extends SingleFragmentActivity {

    @Inject
    ConfigurationManager mConfigurationManager;

    @Override
    protected final Fragment createFragment() {
        if (mConfigurationManager.getPersistentConfiguration()
                                 .isUpcomingAndPastBookingsMergeEnabled()) {
            return UpcomingAndPastBookingsFragment.newInstance();
        }
        else {
            return UpcomingBookingsFragment.newInstance();
        }
    }

    @Override
    public void onBackPressed() {
        //if the over lay fragment is showing, dismiss it and swallow the back press
        ServiceCategoriesOverlayFragment frag
                = (ServiceCategoriesOverlayFragment) getSupportFragmentManager()
                .findFragmentByTag(UpcomingBookingsFragment.mOverlayFragmentTag);
        if (frag != null) {
            frag.animateAndDismissFragment();
        }
        else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onActivityResult(
            final int requestCode,
            final int resultCode,
            final Intent data
    ) {
        super.onActivityResult(requestCode, resultCode, data);
        final Fragment fragment =
                getSupportFragmentManager().findFragmentById(R.id.fragment_container);
        if (fragment != null) {
            fragment.onActivityResult(requestCode, resultCode, data);
        }
    }
}
