package com.handybook.handybook.module.bookings;

import com.google.gson.GsonBuilder;
import com.handybook.handybook.booking.BookingEvent;
import com.handybook.handybook.booking.model.Booking;
import com.handybook.handybook.booking.model.UserBookingsWrapper;
import com.handybook.handybook.util.IOUtils;

import org.junit.Before;
import org.junit.Test;

/**
 * This tests the situation where there are no bookings at all, and no active cleaning plans
 */
public class UpcomingBookingsNoBookingsTest extends UpcomingBookingsBaseTest
{
    @Before
    public void setUp() throws Exception
    {
        super.setup();
        String json = IOUtils.getJsonStringForTest("no_bookings.json");
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

    @Test
    public void testNoCleaningPlan()
    {
        assertNoCleaningPlan();
    }

    @Test
    public void testNoUpcomingBookings()
    {
        assertNoUpcomingBookings();
    }

    @Test
    public void testEmptyViewShowing()
    {
        assertEmptyViewShowing();
    }

}
