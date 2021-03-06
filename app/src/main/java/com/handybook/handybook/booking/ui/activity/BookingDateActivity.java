package com.handybook.handybook.booking.ui.activity;

import android.support.v4.app.Fragment;

import com.handybook.handybook.booking.model.Booking;
import com.handybook.handybook.booking.model.BookingOption;
import com.handybook.handybook.booking.ui.fragment.BookingDateFragment;
import com.handybook.handybook.booking.ui.fragment.BookingDetailFragment;
import com.handybook.handybook.core.constant.BundleKeys;
import com.handybook.handybook.core.model.response.ProAvailabilityResponse;
import com.handybook.handybook.core.ui.activity.SingleFragmentActivity;
import com.handybook.handybook.proteam.viewmodel.ProTeamProViewModel;

import java.util.ArrayList;

public final class BookingDateActivity extends SingleFragmentActivity {

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

            return BookingDateFragment.newInstance(
                    rescheduleBooking,
                    notice,
                    type,
                    providerId,
                    proTeamProViewModel,
                    availabilityResponse,
                    getIntent().getExtras()
            );
        }

        final ArrayList<BookingOption> postOptions
                = getIntent().getParcelableArrayListExtra(BundleKeys.POST_OPTIONS);

        return BookingDateFragment.newInstance(postOptions, getIntent().getExtras());
    }
}
