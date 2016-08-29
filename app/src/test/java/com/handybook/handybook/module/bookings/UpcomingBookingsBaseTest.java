package com.handybook.handybook.module.bookings;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.view.View;

import com.handybook.handybook.RobolectricGradleTestWrapper;
import com.handybook.handybook.booking.BookingEvent;
import com.handybook.handybook.booking.bookingedit.ui.activity.BookingEditFrequencyActivity;
import com.handybook.handybook.booking.ui.activity.BookingDetailActivity;

import org.junit.Ignore;
import org.robolectric.Shadows;
import org.robolectric.shadows.ShadowActivity;
import org.robolectric.shadows.ShadowIntent;

import static org.junit.Assert.assertEquals;

/**
 */
@Ignore
public class UpcomingBookingsBaseTest extends RobolectricGradleTestWrapper
{
    protected UpcomingBookingsFragment mUpcomingBookingsFragment;
    protected BookingEvent.ReceiveBookingsSuccess mBookingReceiveSuccessfulEvent;

    /**
     * Verify that there are upcoming bookings, and that the children can be clicked
     */
    protected void testUpcomingBookingsContainingBookings(UpcomingBookingsFragment fragment, int childCount)
    {
        assertEquals("Upcoming bookings needs to be visible", View.VISIBLE, fragment.mParentBookingsContainer.getVisibility());
        assertEquals(
                "There should be upcoming bookings (with dividers), for a total of " + childCount + " child views",
                childCount,
                fragment.mBookingsContainer.getChildCount()
        );

        //we only test the clicking of the bookings if the above test passes
        if (fragment.mBookingsContainer.getChildCount() == childCount)
        {
            fragment.mBookingsContainer.getChildAt(0).performClick();

            ShadowActivity shadowActivity = Shadows.shadowOf(fragment.getActivity());
            Intent startedIntent = shadowActivity.getNextStartedActivity();
            ShadowIntent shadowIntent = Shadows.shadowOf(startedIntent);
            assertEquals(
                    "Should've launched the BookingDetailActivity, but didn't",
                    BookingDetailActivity.class.getName(),
                    shadowIntent.getIntentClass().getName()
            );
        }
    }

    /**
     * Verify the correctness of the cleaning plans, with the specified number of children
     */
    protected void testCleaningPlanContainingPlans(UpcomingBookingsFragment fragment, int childCount)
    {
        assertEquals("Cleaning plans should be visible", View.VISIBLE, fragment.mExpandableCleaningPlan.getVisibility());
        assertEquals("There should be plans with a dividers in between for a total of " + childCount
                + " views.", childCount, fragment.mExpandableCleaningPlan.mPlanContainer.getChildCount());

        //we only test the clicking of the plans, if the above rule passes
        if (fragment.mExpandableCleaningPlan.mPlanContainer.getChildCount() == childCount)
        {
            //when the plan is clicked, it should launch the rescheduling activity
            fragment.mExpandableCleaningPlan.mPlanContainer.getChildAt(0).performClick();
            ShadowActivity shadowActivity = Shadows.shadowOf(fragment.getActivity());
            Intent startedIntent = shadowActivity.getNextStartedActivity();
            ShadowIntent shadowIntent = Shadows.shadowOf(startedIntent);
            assertEquals(
                    "Should've launched the BookingEditFrequencyActivity, but didn't",
                    BookingEditFrequencyActivity.class.getName(),
                    shadowIntent.getIntentClass().getName()
            );
        }
    }

    /**
     * Active Booking Should be there
     *
     * @throws Exception
     */
    protected void testActiveBookingPresent(UpcomingBookingsFragment fragment, String bookingId)
    {
        //make sure the active booking container is visible
        assertEquals("The active booking container should be visible here", View.VISIBLE, fragment.mActiveBookingContainer.getVisibility());

        //make sure that the active booking fragment is injected.
        Fragment f = fragment.getChildFragmentManager().findFragmentByTag(bookingId);
        assertEquals(true, f != null);
        if (f != null)
        {
            assertEquals("The ActiveBookingFragment fragment should've been present", ActiveBookingFragment.class.getName(), f.getClass().getName());
        }
    }
}
