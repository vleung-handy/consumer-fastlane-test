package com.handybook.handybook.booking.ui.activity;

import android.support.v4.app.Fragment;

import com.handybook.handybook.booking.model.Booking;
import com.handybook.handybook.booking.ui.fragment.BookingReschedulePreferencesFragment;
import com.handybook.handybook.core.constant.BundleKeys;
import com.handybook.handybook.core.ui.activity.MenuDrawerActivity;
import com.handybook.handybook.proteam.model.ProTeam;

public class BookingReschedulePreferencesActivity extends MenuDrawerActivity {

    @Override
    protected Fragment createFragment() {
        final ProTeam.ProTeamCategory category
                = getIntent().getParcelableExtra(BundleKeys.PRO_TEAM_CATEGORY);
        final Booking booking = getIntent().getParcelableExtra(BundleKeys.BOOKING);
        return BookingReschedulePreferencesFragment.newInstance(category, booking);
    }

    @Override
    protected String getNavItemTitle() {
        return null;
    }
}
