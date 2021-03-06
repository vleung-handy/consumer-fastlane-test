package com.handybook.handybook.booking.ui.fragment;

import android.support.v4.app.Fragment;
import android.view.View;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.handybook.handybook.R;
import com.handybook.handybook.RobolectricGradleTestWrapper;
import com.handybook.handybook.booking.BookingEvent;
import com.handybook.handybook.booking.model.Service;
import com.handybook.handybook.core.TestActivity;
import com.handybook.handybook.core.util.TestUtils;
import com.handybook.handybook.library.util.IOUtils;

import org.junit.Ignore;
import org.robolectric.shadows.support.v4.SupportFragmentTestUtil;

import java.util.List;

import static junit.framework.Assert.assertTrue;
import static org.junit.Assert.assertEquals;

/**
 */
@Ignore
public class UpcomingBookingsBaseTest extends RobolectricGradleTestWrapper {

    protected UpcomingBookingsFragment mUpcomingBookingsFragment;

    protected void setup() throws Exception {
        mUpcomingBookingsFragment = UpcomingBookingsFragment.newInstance();
        SupportFragmentTestUtil.startFragment(mUpcomingBookingsFragment, TestActivity.class);

        String json = IOUtils.getJsonStringForTest("services.json");
        List<Service> services = new Gson().fromJson(
                json,
                new TypeToken<List<Service>>() {
                }.getType()
        );
        mUpcomingBookingsFragment.onReceiveServicesSuccess(new BookingEvent.ReceiveServicesSuccess(
                services));

    }

    /**
     * Verify that there are upcoming bookings, and that the children can be clicked
     */
    protected void testUpcomingBookingsContainingBookings(
            UpcomingBookingsFragment fragment,
            int childCount
    ) {
        assertEquals(
                "Upcoming bookings needs to be visible",
                View.VISIBLE,
                fragment.mParentBookingsContainer.getVisibility()
        );
        assertEquals(
                "There should be upcoming bookings (with dividers), for a total of " + childCount +
                " child views",
                childCount,
                fragment.mBookingsContainer.getChildCount()
        );

        //we only test the clicking of the bookings if the above test passes
        if (fragment.mBookingsContainer.getChildCount() == childCount) {
            fragment.mBookingsContainer.getChildAt(0)
                                       .findViewById(R.id.booking_item_container)
                                       .performClick();
            Fragment current = TestUtils.getScreenFragment(fragment.getFragmentManager());
            assertTrue(current instanceof BookingDetailFragment);
        }
    }

    /**
     * Active Booking Should be there
     *
     * @throws Exception
     */
    protected void testActiveBookingPresent(UpcomingBookingsFragment fragment, String bookingId) {
        //make sure the active booking container is visible
        assertEquals(
                "The active booking container should be visible here",
                View.VISIBLE,
                fragment.mActiveBookingContainer.getVisibility()
        );

        //make sure that the active booking fragment is injected.
        Fragment f = fragment.getChildFragmentManager().findFragmentByTag(bookingId);
        assertEquals(true, f != null);
        if (f != null) {
            assertEquals(
                    "The ActiveBookingFragment fragment should've been present",
                    ActiveBookingFragment.class.getName(),
                    f.getClass().getName()
            );
        }
    }

    protected void assertNoActiveBooking() {
        assertEquals(
                "The active booking container should NOT be visible here",
                View.GONE,
                mUpcomingBookingsFragment.mActiveBookingContainer.getVisibility()
        );
    }

    protected void assertNoUpcomingBookings() {
        assertEquals(
                "Upcoming bookings section should not be visible",
                View.GONE,
                mUpcomingBookingsFragment.mParentBookingsContainer.getVisibility()
        );
    }

    protected void assertEmptyViewShowing() {
        assertEquals(
                "The empty view should be visible",
                View.VISIBLE,
                mUpcomingBookingsFragment.mNoBookingsView.getVisibility()
        );
    }

    protected void assertEmptyViewNotShowing() {
        assertEquals(
                "The empty view should be visible",
                View.GONE,
                mUpcomingBookingsFragment.mNoBookingsView.getVisibility()
        );
    }
}
