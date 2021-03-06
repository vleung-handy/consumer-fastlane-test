package com.handybook.handybook.test.booking;

import com.handybook.handybook.core.ui.activity.SplashActivity;
import com.handybook.handybook.tool.data.TestUsers;
import com.handybook.handybook.tool.model.TestUser;
import com.handybook.handybook.tool.util.LauncherActivityTestRule;

import org.junit.Rule;

public class CancelRecurringBookingTest {

    @Rule
    public LauncherActivityTestRule<SplashActivity> mActivityRule =
            new LauncherActivityTestRule<>(SplashActivity.class);

    private static final TestUser TEST_USER = TestUsers.CANCEL_RECURRING_USER;

    /**
     * test that we can access the cancel recurring webview from the upcoming bookings page
     */
    //    @Test
    //    public void testCancelRecurringBooking() {
    //        AppInteractionUtil.logOutAndPassOnboarding();
    //        AppInteractionUtil.logIn(TEST_USER);
    //        AppInteractionUtil.waitForServiceCategoriesPage();
    //
    //        //Go to My Bookings
    //        onView(withId(R.id.bookings)).perform(click());
    //
    //        // Tap on the first booking
    //        ViewUtil.waitForViewVisible(
    //                R.id.button_plan_expand,
    //                ViewUtil.LONG_MAX_WAIT_TIME_MS
    //        );
    //        onView(withId(R.id.button_plan_expand)).perform(click());
    //        onView(withId(R.id.button_edit)).perform(click());
    //        onView(withId(R.id.edit_plan_cancel)).perform(click());
    //
    //        /*just check that we're showing the webview
    //        currently unable to test for specific elements inside the webview
    //         */
    //        ViewUtil.waitForViewVisible(R.id.handy_web_view, ViewUtil.LONG_MAX_WAIT_TIME_MS);
    //    }
}
