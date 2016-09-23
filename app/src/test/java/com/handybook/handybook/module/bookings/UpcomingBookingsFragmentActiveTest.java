package com.handybook.handybook.module.bookings;

import com.google.gson.GsonBuilder;
import com.handybook.handybook.booking.BookingEvent;
import com.handybook.handybook.booking.model.Booking;
import com.handybook.handybook.booking.model.UserBookingsWrapper;
import com.handybook.handybook.library.util.IOUtils;

import org.junit.Before;
import org.junit.Test;

/**
 * Tests all the different parts that relates to having an active booking, upcoming bookings
 * and the existence of a cleaning plan.
 */
public class UpcomingBookingsFragmentActiveTest extends UpcomingBookingsBaseTest
{
    @Before
    public void setUp() throws Exception
    {
        super.setup();
        String json = IOUtils.getJsonStringForTest("upcoming_bookings.json");
        final UserBookingsWrapper activeBooking = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ssX")
                .create()
                .fromJson(json, UserBookingsWrapper.class);

        mBookingReceiveSuccessfulEvent = new BookingEvent.ReceiveBookingsSuccess(activeBooking, Booking.List.VALUE_ONLY_BOOKINGS_UPCOMING);
        mUpcomingBookingsFragment.onReceiveBookingsSuccess(mBookingReceiveSuccessfulEvent);
    }

    /**
     * Active Booking Should be there
     *
     * @throws Exception
     */
    @Test
    public void testActiveBookingSection() throws Exception
    {
        testActiveBookingPresent(mUpcomingBookingsFragment, "195370");
    }

    /**
     * Verify that there are upcoming bookings
     */
    @Test
    public void testUpcomingBookingsSection()
    {
        //6 bookings + one share banner
        testUpcomingBookingsContainingBookings(mUpcomingBookingsFragment, 7);
    }

    /**
     * Verify the correctness of the cleaning plans
     */
    @Test
    public void testCleaningPlan()
    {
        testCleaningPlanContainingPlans(mUpcomingBookingsFragment, 3);
    }

    @Test
    public void testEmptyViewNotShowing()
    {
        assertEmptyViewNotShowing();
    }

}
