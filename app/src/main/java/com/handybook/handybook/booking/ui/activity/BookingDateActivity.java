package com.handybook.handybook.booking.ui.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.handybook.handybook.booking.model.Booking;
import com.handybook.handybook.booking.model.BookingOption;
import com.handybook.handybook.booking.ui.fragment.BookingDateFragment;
import com.handybook.handybook.booking.ui.fragment.BookingDetailFragment;
import com.handybook.handybook.core.constant.BundleKeys;
import com.handybook.handybook.core.model.response.ProAvailabilityResponse;
import com.handybook.handybook.core.ui.activity.MenuDrawerActivity;
import com.handybook.handybook.proteam.viewmodel.ProTeamProViewModel;

import java.util.ArrayList;

public final class BookingDateActivity extends MenuDrawerActivity {

    @Override
    protected final Fragment createFragment() {

        Booking rescheduleBooking = getIntent().getParcelableExtra(BundleKeys.RESCHEDULE_BOOKING);
        if (rescheduleBooking != null) {
            final String notice = getIntent().getStringExtra(BundleKeys.RESCHEDULE_NOTICE);

            final BookingDetailFragment.RescheduleType type = (BookingDetailFragment.RescheduleType)
                    getIntent().getSerializableExtra(BundleKeys.RESCHEDULE_TYPE);

            final String providerId = getIntent().getStringExtra(BundleKeys.PROVIDER_ID);
            final ProTeamProViewModel proTeamProViewModel =
                    (ProTeamProViewModel) getIntent().getSerializableExtra(BundleKeys.PRO_TEAM_PRO);

            final ProAvailabilityResponse availabilityResponse = (ProAvailabilityResponse)
                    getIntent().getSerializableExtra(BundleKeys.PRO_AVAILABILITY);

            Fragment fragment = BookingDateFragment.newInstance(
                    rescheduleBooking,
                    notice,
                    type,
                    providerId,
                    proTeamProViewModel,
                    availabilityResponse
            );
            fragment.setArguments(getIntent().getExtras());
            return fragment;
        }

        final ArrayList<BookingOption> postOptions
                = getIntent().getParcelableArrayListExtra(BundleKeys.POST_OPTIONS);

        return BookingDateFragment.newInstance(postOptions);
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
