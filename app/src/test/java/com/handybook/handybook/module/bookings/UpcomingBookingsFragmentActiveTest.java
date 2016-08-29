package com.handybook.handybook.module.bookings;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.handybook.handybook.RobolectricGradleTestWrapper;
import com.handybook.handybook.booking.BookingEvent;
import com.handybook.handybook.booking.bookingedit.ui.activity.BookingEditFrequencyActivity;
import com.handybook.handybook.booking.model.Booking;
import com.handybook.handybook.booking.model.Service;
import com.handybook.handybook.booking.model.UserBookingsWrapper;
import com.handybook.handybook.booking.ui.activity.BookingDetailActivity;
import com.handybook.handybook.util.IoUtils;

import org.junit.Before;
import org.junit.Test;
import org.robolectric.Shadows;
import org.robolectric.shadows.ShadowActivity;
import org.robolectric.shadows.ShadowIntent;
import org.robolectric.shadows.support.v4.SupportFragmentTestUtil;

import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * Tests all the different parts that relates to having an active booking, upcoming bookings
 * and the existence of a cleaning plan.
 */
public class UpcomingBookingsFragmentActiveTest extends RobolectricGradleTestWrapper
{

    private UpcomingBookingsFragment mFragment;
    private BookingEvent.ReceiveBookingsSuccess mBookingReceiveSuccessfulEvent;

    @Before
    public void setUp() throws Exception
    {
        mFragment = UpcomingBookingsFragment.newInstance();
        String json = IoUtils.getJsonString("upcoming_bookings.json");
        final UserBookingsWrapper activeBooking = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ssX")
                .create()
                .fromJson(json, UserBookingsWrapper.class);

        json = IoUtils.getJsonString("services.json");
        List<Service> services = new Gson().fromJson(
                json,
                new TypeToken<List<Service>>()
                {
                }.getType());

        SupportFragmentTestUtil.startFragment(mFragment, AppCompatActivity.class);

        mBookingReceiveSuccessfulEvent = new BookingEvent.ReceiveBookingsSuccess(activeBooking, Booking.List.VALUE_ONLY_BOOKINGS_UPCOMING);
        mFragment.onReceiveBookingsSuccess(mBookingReceiveSuccessfulEvent);
        mFragment.onReceiveServicesSuccess(new BookingEvent.ReceiveServicesSuccess(services));
    }

    /**
     * Active Booking Should be there
     *
     * @throws Exception
     */
    @Test
    public void testActiveBookingSection() throws Exception
    {
        //make sure the active booking container is visible
        assertEquals("The active booking container should be visible here", View.VISIBLE, mFragment.mActiveBookingContainer.getVisibility());

        //make sure that the active booking fragment is injected.
        Fragment fragment = mFragment.getChildFragmentManager().findFragmentByTag("195370");
        assertEquals(true, fragment != null);
        if (fragment != null)
        {
            assertEquals("The ActiveBookingFragment fragment should've been present", ActiveBookingFragment.class.getName(), fragment.getClass().getName());
        }
    }

    /**
     * Verify that there are upcoming bookings
     */
    @Test
    public void testUpcomingBookingsSection()
    {
        assertEquals("Upcoming bookings needs to be visible", View.VISIBLE, mFragment.mParentBookingsContainer.getVisibility());
        assertEquals("There should be 3 upcoming bookings (with 3 dividers), for a total of 6 child views", 6, mFragment.mBookingsContainer.getChildCount());

        //we only test the clicking of the bookings if the above test passes
        if (mFragment.mBookingsContainer.getChildCount() == 6)
        {
            mFragment.mBookingsContainer.getChildAt(0).performClick();

            ShadowActivity shadowActivity = Shadows.shadowOf(mFragment.getActivity());
            Intent startedIntent = shadowActivity.getNextStartedActivity();
            ShadowIntent shadowIntent = Shadows.shadowOf(startedIntent);
            assertEquals("Should've launched the BookingDetailActivity, but didn't", BookingDetailActivity.class.getName(), shadowIntent.getIntentClass().getName());
        }
    }

    /**
     * Verify the correctness of the cleaning plans
     */
    @Test
    public void testCleaningPlan()
    {
        assertEquals("Cleaning plans should be visible", View.VISIBLE, mFragment.mExpandableCleaningPlan.getVisibility());
        assertEquals("There should be 2 plans with a divider in between for a total of 3 views.", 3, mFragment.mExpandableCleaningPlan.mPlanContainer.getChildCount());

        //we only test the clicking of the plans, if the above rule passes
        if (mFragment.mExpandableCleaningPlan.mPlanContainer.getChildCount() == 3)
        {
            //when the plan is clicked, it should launch the rescheduling activity
            mFragment.mExpandableCleaningPlan.mPlanContainer.getChildAt(0).performClick();
            ShadowActivity shadowActivity = Shadows.shadowOf(mFragment.getActivity());
            Intent startedIntent = shadowActivity.getNextStartedActivity();
            ShadowIntent shadowIntent = Shadows.shadowOf(startedIntent);
            assertEquals("Should've launched the BookingEditFrequencyActivity, but didn't", BookingEditFrequencyActivity.class.getName(), shadowIntent.getIntentClass().getName());
        }
    }
}
