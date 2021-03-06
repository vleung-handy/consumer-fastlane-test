package com.handybook.handybook.test.booking;

import com.handybook.handybook.R;
import com.handybook.handybook.core.ui.activity.SplashActivity;
import com.handybook.handybook.tool.data.TestUsers;
import com.handybook.handybook.tool.model.TestUser;
import com.handybook.handybook.tool.util.AppInteractionUtil;
import com.handybook.handybook.tool.util.LauncherActivityTestRule;
import com.handybook.handybook.tool.util.ViewUtil;

import org.junit.Rule;
import org.junit.Test;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.scrollTo;
import static android.support.test.espresso.assertion.ViewAssertions.doesNotExist;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

public class CancelBookingTest {

    @Rule
    public LauncherActivityTestRule<SplashActivity> mActivityRule =
            new LauncherActivityTestRule<>(SplashActivity.class);

    /**
     * this user has only one upcoming booking
     */
    private static final TestUser TEST_USER = TestUsers.CANCEL_SINGLE_BOOKING_USER;

    @Test
    public void testCancelBooking() {
        AppInteractionUtil.logOutAndPassOnboarding();
        AppInteractionUtil.logIn(TEST_USER);
        AppInteractionUtil.waitForServiceCategoriesPage();

        //Go to My Bookings
        onView(withId(R.id.bookings)).perform(click());

        // Tap on the (only) booking
        ViewUtil.waitForViewVisible(
                R.id.booking_item_container,
                ViewUtil.LONG_MAX_WAIT_TIME_MS
        );
        onView(withId(R.id.booking_item_title)).check(matches(isDisplayed())); // Check if booking row is there
        onView(withId(R.id.booking_item_container)).perform(click());

        // Click on cancel booking
        ViewUtil.waitForViewInScrollViewVisible(
                R.id.action_button_cancel_booking,
                ViewUtil.LONG_MAX_WAIT_TIME_MS
        );
        onView(withId(R.id.action_button_cancel_booking)).perform(scrollTo()).perform(click());

        // Select the first reason and click cancel booking
        ViewUtil.waitForViewVisible(
                R.id.booking_cancel_reason_button,
                ViewUtil.LONG_MAX_WAIT_TIME_MS
        );
        onView(ViewUtil.first(withId(R.id.check_box))).perform(click());
        onView(withId(R.id.booking_cancel_reason_button)).perform(click());

        // If upcoming bookings page is visible and no upcoming bookings displayed, then cancel booking worked
        onView(withId(R.id.booking_item_container)).check(doesNotExist()); // Check if booking row is there
    }
}
