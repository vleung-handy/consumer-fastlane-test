package com.handybook.handybook.proteam.ui.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;

import com.handybook.handybook.R;
import com.handybook.handybook.booking.model.Booking;
import com.handybook.handybook.core.constant.BundleKeys;
import com.handybook.handybook.core.ui.activity.BaseActivity;
import com.handybook.handybook.proteam.model.ProTeam;
import com.handybook.handybook.proteam.ui.fragment.BookingProTeamRescheduleFragment;

import butterknife.Bind;
import butterknife.ButterKnife;

public class BookingProTeamRescheduleActivity extends BaseActivity {
    @Bind(R.id.toolbar)
    Toolbar mToolbar;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_pro_team_reschedule_list);
        ButterKnife.bind(this);
        setSupportActionBar(mToolbar, getString(R.string.choose_a_pro), true, R.drawable.ic_back);

        //Set the fragment
        ProTeam.ProTeamCategory category
                = getIntent().getParcelableExtra(BundleKeys.PRO_TEAM_CATEGORY);
        Booking booking = getIntent().getParcelableExtra(BundleKeys.BOOKING);
        Fragment fragment = BookingProTeamRescheduleFragment.newInstance(category, booking);

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.reschedule_pro_team_fragment_container, fragment)
                .commit();
    }
}
