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
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

public class CancelRecurringBookingTest {

    @Rule
    public LauncherActivityTestRule<ServiceCategoriesActivity> mActivityRule =
            new LauncherActivityTestRule<>(ServiceCategoriesActivity.class);

    private static final TestUser TEST_USER = TestUsers.CANCEL_RECURRING_USER;

    /**
     * test that we can access the cancel recurring webview from the upcoming bookings page
     */
    @Test
    public void testCancelRecurringBooking() {
        AppInteractionUtil.logOutAndPassOnboarding();
        AppInteractionUtil.logIn(TEST_USER);
        AppInteractionUtil.waitForServiceCategoriesPage();

        //Go to My Bookings
        AppInteractionUtil.openDrawer();

        //need to wait for the nav link because it doesn't appear immediately
        ViewUtil.waitForTextVisible(R.string.my_bookings, ViewUtil.SHORT_MAX_WAIT_TIME_MS);
        onView(withText(R.string.my_bookings)).perform(click());

        // Tap on the first booking
        ViewUtil.waitForViewVisible(
                R.id.button_plan_expand,
                ViewUtil.LONG_MAX_WAIT_TIME_MS
        );
        onView(withId(R.id.button_plan_expand)).perform(click());
        onView(withId(R.id.button_edit)).perform(click());
        onView(withId(R.id.edit_plan_cancel)).perform(click());

        /*just check that we're showing the webview
        currently unable to test for specific elements inside the webview
         */
        ViewUtil.waitForViewVisible(R.id.handy_web_view, ViewUtil.LONG_MAX_WAIT_TIME_MS);
    }
}
