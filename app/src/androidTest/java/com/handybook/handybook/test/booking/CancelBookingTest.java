package com.handybook.handybook.test.booking;

import com.handybook.handybook.R;
import com.handybook.handybook.booking.ui.activity.ServiceCategoriesActivity;
import com.handybook.handybook.test.LauncherActivityTestRule;
import com.handybook.handybook.test.data.TestUsers;
import com.handybook.handybook.test.model.TestUser;
import com.handybook.handybook.test.util.AppInteractionUtil;
import com.handybook.handybook.test.util.ViewUtil;

import org.junit.Rule;
import org.junit.Test;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.scrollTo;
import static android.support.test.espresso.assertion.ViewAssertions.doesNotExist;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

public class CancelBookingTest
{
    @Rule
    public LauncherActivityTestRule<ServiceCategoriesActivity> mActivityRule =
            new LauncherActivityTestRule<>(ServiceCategoriesActivity.class);

    /**
     * this user has only one upcoming booking
     */
    private static final TestUser TEST_USER = TestUsers.CANCEL_SINGLE_BOOKING_USER;

    @Test
    public void testCancelBooking()
    {
        AppInteractionUtil.logOutAndPassOnboarding();
        AppInteractionUtil.logIn(TEST_USER);
        AppInteractionUtil.waitForServiceCategoriesPage();

        //Go to My Bookings
        AppInteractionUtil.openDrawer();

        //the "my bookings" nav seems to be added async
        ViewUtil.waitForTextVisible(R.string.my_bookings, ViewUtil.LONG_MAX_WAIT_TIME_MS);
        onView(withText(R.string.my_bookings)).perform(click());

        // Tap on the (only) booking
        ViewUtil.waitForViewVisible(
                R.id.booking_item_container,
                ViewUtil.LONG_MAX_WAIT_TIME_MS
        );
        onView(withId(R.id.text_booking_title)).check(matches(isDisplayed())); // Check if booking row is there
        onView(withId(R.id.booking_item_container)).perform(click());

        // Click on cancel booking
        ViewUtil.waitForViewInScrollViewVisible(
                R.id.action_button_cancel_booking,
                ViewUtil.LONG_MAX_WAIT_TIME_MS
        );
        onView(withId(R.id.action_button_cancel_booking)).perform(scrollTo()).perform(click());

        // Select the first reason and click cancel booking
        ViewUtil.waitForViewVisible(R.id.cancel_button, ViewUtil.LONG_MAX_WAIT_TIME_MS);
        onView(ViewUtil.first(withId(R.id.check_box))).perform(click());
        onView(withId(R.id.cancel_button)).perform(click());

        // If upcoming bookings page is visible and no upcoming bookings displayed, then cancel booking worked
        onView(withId(R.id.booking_item_container)).check(doesNotExist()); // Check if booking row is there
    }
}
