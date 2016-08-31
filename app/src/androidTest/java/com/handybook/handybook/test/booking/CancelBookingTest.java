package com.handybook.handybook.test.booking;


import android.support.test.espresso.contrib.DrawerActions;
import android.support.test.rule.ActivityTestRule;

import com.handybook.handybook.R;
import com.handybook.handybook.booking.ui.activity.ServiceCategoriesActivity;
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
    public ActivityTestRule<ServiceCategoriesActivity> mActivityRule =
            new ActivityTestRule<>(ServiceCategoriesActivity.class);

    private static final TestUser TEST_USER = TestUsers.CANCEL_SINGLE_BOOKING_USER;

    @Test
    public void testCancelBooking()
    {
        AppInteractionUtil.logOutAndPassOnboarding();
        AppInteractionUtil.logIn(TEST_USER);

        //wait for network call to return with service list
        ViewUtil.waitForViewVisible(R.id.recycler_view, ViewUtil.LONG_MAX_WAIT_TIME_MS);

        //Go to My Bookings
        DrawerActions.openDrawer(R.id.drawer_layout);
        onView(withText(R.string.my_bookings)).perform(click());

        // Tap on the first booking
        ViewUtil.waitForViewVisible(R.id.ll_booking_card_booking_row_container,
                ViewUtil.LONG_MAX_WAIT_TIME_MS);
        onView(withId(R.id.tv_card_booking_row_title)).check(matches(isDisplayed())); // Check if booking row is there
        onView(ViewUtil.nthChildOf(withId(R.id.ll_booking_card_booking_row_container), 0))
                .perform(click());

        // Click on cancel booking
        ViewUtil.waitForViewInScrollViewVisible(R.id.action_button_cancel_booking, ViewUtil.LONG_MAX_WAIT_TIME_MS);
        onView(withId(R.id.action_button_cancel_booking)).perform(scrollTo()).perform(click());

        // Select the first reason and click cancel booking
        ViewUtil.waitForViewVisible(R.id.cancel_button, ViewUtil.LONG_MAX_WAIT_TIME_MS);
        onView(ViewUtil.first(withId(R.id.check_box))).perform(click());
        onView(withId(R.id.cancel_button)).perform(click());

        // If upcoming bookings page is visible and no upcoming bookings displayed, then cancel booking worked
        onView(withId(R.id.tv_card_booking_row_title)).check(doesNotExist()); // Check if booking row is there
    }
}
