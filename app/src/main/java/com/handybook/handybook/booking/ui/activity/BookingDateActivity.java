package com.handybook.handybook.booking.ui.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.handybook.handybook.booking.model.Booking;
import com.handybook.handybook.booking.model.BookingOption;
import com.handybook.handybook.booking.ui.fragment.BookingDateFragmentV2;
import com.handybook.handybook.booking.ui.fragment.BookingDetailFragment;
import com.handybook.handybook.core.constant.BundleKeys;
import com.handybook.handybook.core.ui.activity.MenuDrawerActivity;

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

            return BookingDateFragmentV2.newInstance(rescheduleBooking, notice, type, providerId);
        }

        final ArrayList<BookingOption> postOptions
                = getIntent().getParcelableArrayListExtra(BundleKeys.POST_OPTIONS);

        //config will be removed soon
        return BookingDateFragmentV2.newInstance(postOptions);
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
