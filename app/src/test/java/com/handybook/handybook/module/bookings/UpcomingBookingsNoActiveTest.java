package com.handybook.handybook.module.bookings;

import com.google.gson.GsonBuilder;
import com.handybook.handybook.booking.BookingEvent;
import com.handybook.handybook.booking.model.Booking;
import com.handybook.handybook.booking.model.UserBookingsWrapper;
import com.handybook.handybook.util.IOUtils;

import org.junit.Before;
import org.junit.Test;

/**
 * This is to test the situation where there is no active booking at all in the upcoming bookings page
 */
public class UpcomingBookingsNoActiveTest extends UpcomingBookingsBaseTest
{
    @Before
    public void setUp() throws Exception
    {
        super.setup();
        String json = IOUtils.getJsonStringForTest("upcoming_no_active_bookings.json");
        final UserBookingsWrapper bookings = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ssX")
                .create()
                .fromJson(json, UserBookingsWrapper.class);

        mBookingReceiveSuccessfulEvent = new BookingEvent.ReceiveBookingsSuccess(bookings, Booking.List.VALUE_ONLY_BOOKINGS_UPCOMING);
        mUpcomingBookingsFragment.onReceiveBookingsSuccess(mBookingReceiveSuccessfulEvent);
    }

    @Test
    public void testNoActiveBooking()
    {
        assertNoActiveBooking();
    }

    /**
     * Verify the correctness of the cleaning plans
     */
    @Test
    public void testCleaningPlan()
    {
        testCleaningPlanContainingPlans(mUpcomingBookingsFragment, 3);
    }

    /**
     * Verify that there are upcoming bookings
     */
    @Test
    public void testUpcomingBookingsSection()
    {
        testUpcomingBookingsContainingBookings(mUpcomingBookingsFragment, 8);
    }

    @Test
    public void testEmptyViewNotShowing()
    {
        assertEmptyViewNotShowing();
    }
}

