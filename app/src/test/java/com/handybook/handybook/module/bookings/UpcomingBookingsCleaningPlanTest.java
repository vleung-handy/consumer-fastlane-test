package com.handybook.handybook.module.bookings;

import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.handybook.handybook.booking.BookingEvent;
import com.handybook.handybook.booking.model.Booking;
import com.handybook.handybook.booking.model.Service;
import com.handybook.handybook.booking.model.UserBookingsWrapper;
import com.handybook.handybook.util.IoUtils;

import org.junit.Before;
import org.junit.Test;
import org.robolectric.shadows.support.v4.SupportFragmentTestUtil;

import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * This is to test the situation where there are no cleaning plans on the upcoming bookings page
 */
public class UpcomingBookingsCleaningPlanTest extends UpcomingBookingsBaseTest
{

    @Before
    public void setUp() throws Exception
    {
        mUpcomingBookingsFragment = UpcomingBookingsFragment.newInstance();
        String json = IoUtils.getJsonString("upcoming_bookings_no_plan.json");
        final UserBookingsWrapper bookings = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ssX")
                .create()
                .fromJson(json, UserBookingsWrapper.class);

        json = IoUtils.getJsonString("services.json");
        List<Service> services = new Gson().fromJson(
                json,
                new TypeToken<List<Service>>()
                {
                }.getType());

        SupportFragmentTestUtil.startFragment(mUpcomingBookingsFragment, AppCompatActivity.class);

        mBookingReceiveSuccessfulEvent = new BookingEvent.ReceiveBookingsSuccess(bookings, Booking.List.VALUE_ONLY_BOOKINGS_UPCOMING);
        mUpcomingBookingsFragment.onReceiveBookingsSuccess(mBookingReceiveSuccessfulEvent);
        mUpcomingBookingsFragment.onReceiveServicesSuccess(new BookingEvent.ReceiveServicesSuccess(services));
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
     * Cleaning plans should not be visible
     */
    @Test
    public void testCleaningPlanVisibility()
    {
        assertEquals("Cleaning plans should not be visible", View.GONE, mUpcomingBookingsFragment.mExpandableCleaningPlan.getVisibility());
    }

    /**
     * Verify that there are upcoming bookings
     */
    @Test
    public void testUpcomingBookingsSection()
    {
        testUpcomingBookingsContainingBookings(mUpcomingBookingsFragment, 6);
    }

}
