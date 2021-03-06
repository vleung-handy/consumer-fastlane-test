package com.handybook.handybook.booking.ui.fragment;

import com.google.gson.GsonBuilder;
import com.handybook.handybook.booking.model.UserBookingsWrapper;
import com.handybook.handybook.library.util.IOUtils;

import org.junit.Before;
import org.junit.Test;

/**
 * This is to test the situation where there is no active booking at all in the upcoming bookings page
 */
public class UpcomingBookingsNoActiveTest extends UpcomingBookingsBaseTest {

    @Before
    public void setUp() throws Exception {
        super.setup();
        String json = IOUtils.getJsonStringForTest("upcoming_no_active_bookings.json");
        final UserBookingsWrapper bookings = new GsonBuilder().setDateFormat(
                "yyyy-MM-dd'T'HH:mm:ssX")
                                                              .create()
                                                              .fromJson(
                                                                      json,
                                                                      UserBookingsWrapper.class
                                                              );

        mUpcomingBookingsFragment.onReceiveBookingsSuccess(bookings);
    }

    @Test
    public void testNoActiveBooking() {
        assertNoActiveBooking();
    }

    /**
     * Verify that there are upcoming bookings
     */
    @Test
    public void testUpcomingBookingsSection() {
        //8 bookings + one share banner
        testUpcomingBookingsContainingBookings(mUpcomingBookingsFragment, 9);
    }

    @Test
    public void testEmptyViewNotShowing() {
        assertEmptyViewNotShowing();
    }
}

