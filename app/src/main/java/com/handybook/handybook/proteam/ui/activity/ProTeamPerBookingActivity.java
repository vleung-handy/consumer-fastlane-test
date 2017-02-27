package com.handybook.handybook.proteam.ui.activity;

import android.support.v4.app.Fragment;

import com.handybook.handybook.R;
import com.handybook.handybook.booking.model.Booking;
import com.handybook.handybook.core.constant.BundleKeys;
import com.handybook.handybook.core.ui.activity.MenuDrawerActivity;
import com.handybook.handybook.proteam.model.ProTeam;
import com.handybook.handybook.proteam.ui.fragment.BookingProTeamConversationsFragment;

public class ProTeamPerBookingActivity extends MenuDrawerActivity {

    @Override
    protected Fragment createFragment() {
        ProTeam.ProTeamCategory category
                = getIntent().getParcelableExtra(BundleKeys.PRO_TEAM_CATEGORY);
        Booking booking = getIntent().getParcelableExtra(BundleKeys.BOOKING);
        return BookingProTeamConversationsFragment.newInstance(category, booking);
    }

    @Override
    protected String getNavItemTitle() {
        return getString(R.string.title_activity_pro_team);
    }
}
