package com.handybook.handybook;

import android.test.ActivityInstrumentationTestCase2;

import com.handybook.handybook.booking.ui.activity.ServiceCategoriesActivity;
import com.handybook.handybook.testdata.TestUser;
import com.handybook.handybook.testutil.AppInteractionUtils;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

//note that animations should be disabled on the device running these tests
public class LoginTest extends ActivityInstrumentationTestCase2
{
    private final TestUser mTestUser = TestUser.TEST_USER_NY;

    public LoginTest()
    {
        super(ServiceCategoriesActivity.class);
    }

    @Override
    protected void setUp() throws Exception
    {
        super.setUp();
        getActivity();
    }

    @Override
    protected void tearDown() throws Exception
    {
        super.tearDown();
    }

    /**
     * Logs in as the test user and then logs out
     *
     * Assumptions:
     * - no one is logged into the app
     * - there are no popup modals (for example, promos)
     */
    public void testCanLogInAndLogOut()
    {
        AppInteractionUtils.clickGetStartedButtonIfPresent();

        AppInteractionUtils.clickOpenNavigationMenuButton();

        /* log in as the test user */
        //click the login tab
        onView(withId(R.id.nav_menu_log_in)).perform(click());

        //input credentials
        onView(withId(R.id.email_text)).
                perform(click(), typeText(mTestUser.getEmail()), closeSoftKeyboard());
        onView(withId(R.id.password_text)).
                perform(click(), typeText(mTestUser.getPassword()), closeSoftKeyboard());

        //click the login button
        onView(withId(R.id.login_button)).perform(click());

        //wait for progress dialog
        AppInteractionUtils.waitForProgressDialog();

        AppInteractionUtils.clickOpenNavigationMenuButton();

        //click the log out button
        onView(withId(R.id.nav_menu_log_out)).perform(click());

        onView(withId(R.id.button_positive_label)).perform(click());

        AppInteractionUtils.clickOpenNavigationMenuButton();

        //verify that the navigation bar says "log in"
        onView(withId(R.id.nav_menu_log_in)).check(matches(isDisplayed()));
    }
}
