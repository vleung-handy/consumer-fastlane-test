package com.handybook.handybook.booking.ui.fragment;

import android.view.View;

import com.google.gson.GsonBuilder;
import com.handybook.handybook.booking.BookingEvent;
import com.handybook.handybook.booking.model.Booking;
import com.handybook.handybook.booking.model.UserBookingsWrapper;
import com.handybook.handybook.library.util.IOUtils;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * This is to test the situation where there are no cleaning plans on the upcoming bookings page
 */
public class UpcomingBookingsCleaningPlanTest extends UpcomingBookingsBaseTest {

    @Before
    public void setUp() throws Exception {
        super.setup();
        String json = IOUtils.getJsonStringForTest("upcoming_bookings_no_plan.json");
        final UserBookingsWrapper bookings = new GsonBuilder().setDateFormat(
                "yyyy-MM-dd'T'HH:mm:ssX")
                                                              .create()
                                                              .fromJson(
                                                                      json,
                                                                      UserBookingsWrapper.class
                                                              );

        mBookingReceiveSuccessfulEvent = new BookingEvent.ReceiveBookingsSuccess(
                bookings,
                Booking.List.VALUE_ONLY_BOOKINGS_UPCOMING
        );
        mUpcomingBookingsFragment.onReceiveBookingsSuccess(mBookingReceiveSuccessfulEvent);
    }

    /**
     * Active Booking Should be there
     *
     * @throws Exception
     */
    @Test
    public void testActiveBookingSection() throws Exception {
        testActiveBookingPresent(mUpcomingBookingsFragment, "195370");
    }

    /**
     * Cleaning plans should not be visible
     */
    @Test
    public void testCleaningPlanVisibility() {
        assertEquals(
                "Cleaning plans should not be visible",
                View.GONE,
                mUpcomingBookingsFragment.mExpandableCleaningPlan.getVisibility()
        );
    }

    /**
     * Verify that there are upcoming bookings
     */
    @Test
    public void testUpcomingBookingsSection() {
        //6 bookings + one share banner
        testUpcomingBookingsContainingBookings(mUpcomingBookingsFragment, 7);
    }

    @Test
    public void testEmptyViewNotShowing() {
        assertEmptyViewNotShowing();
    }

}
