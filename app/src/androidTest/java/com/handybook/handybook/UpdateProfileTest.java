package com.handybook.handybook;

import android.app.Activity;
import android.test.ActivityInstrumentationTestCase2;

import com.handybook.handybook.testdata.TestUser;
import com.handybook.handybook.testutil.AppInteractionUtils;
import com.handybook.handybook.testutil.ViewUtils;
import com.handybook.handybook.ui.activity.ServiceCategoriesActivity;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.replaceText;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

public class UpdateProfileTest extends ActivityInstrumentationTestCase2
{
    private Activity mActivity;
    private final TestUser mTestUser = TestUser.TEST_USER_UPDATE_PROFILE;

    public UpdateProfileTest()
    {
        super(ServiceCategoriesActivity.class);
    }

    @Override
    protected void setUp() throws Exception
    {
        super.setUp();
        mActivity = getActivity();
    }

    @Override
    protected void tearDown() throws Exception
    {
        super.tearDown();
    }

    /**
     * Logs in as the test user and updates their profile
     * with a new phone number and password
     *
     * Assumptions:
     * - no one is logged into the app
     * - there are no popup modals (for example, promos)
     *
     * TODO: can we make this use a persistent auth token so we don't have to log in?
     */
    public void testCanUpdateProfile()
    {
        AppInteractionUtils.clickGetStartedButtonIfPresent();

        AppInteractionUtils.clickOpenNavigationMenuButton();

        /* log in as the test user */
        //click the login tab
        onView(withId(R.id.nav_menu_log_in)).perform(click());

        //input credentials
        onView(withId(R.id.email_text)).perform(click()).
                perform(typeText(mTestUser.getEmail()));
        onView(withId(R.id.password_text)).perform(click()).
                perform(typeText(mTestUser.getPassword()));

        //click the login button
        onView(withId(R.id.login_button)).perform(click());

        //wait for progress dialog
        AppInteractionUtils.waitForProgressDialog();

        AppInteractionUtils.waitForNavMenuDanceAndHomeScreen();

        /* update the test user's phone and password*/
        //click the nav button
        AppInteractionUtils.clickOpenNavigationMenuButton();

        //click the profile tab
        onView(withId(R.id.nav_menu_profile)).perform(click());

        //wait for progress dialog
        AppInteractionUtils.waitForProgressDialog();

        //replace the phone number text
        onView(withId(R.id.phone_text)).perform(replaceText("9876543210"));

        onView(withId(R.id.old_password_text)).perform(click()).perform(typeText(mTestUser.getPassword()));
        onView(withId(R.id.new_password_text)).perform(click()).perform(typeText("newpassword"));

        //press the update button
        onView(withId(R.id.update_button)).perform(click());

        //wait for progress dialog
        AppInteractionUtils.waitForProgressDialog();

        ViewUtils.checkToastDisplayed(R.string.info_updated, mActivity);
    }

}
