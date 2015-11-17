package com.handybook.handybook;

import android.test.ActivityInstrumentationTestCase2;
import android.widget.ImageButton;

import com.handybook.handybook.testutil.ViewUtils;
import com.handybook.handybook.ui.activity.ServiceCategoriesActivity;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.replaceText;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.matcher.ViewMatchers.isAssignableFrom;
import static android.support.test.espresso.matcher.ViewMatchers.withContentDescription;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;

public class UpdateProfileTest extends ActivityInstrumentationTestCase2
{

    public UpdateProfileTest()
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
     * assumptions:
     * user A is logged in with the following attributes:
     * phone number: 1234567890
     * password: password
     */
    public void testCanUpdateProfile() {

//        onView(withId(R.id.start_button)).perform(click());

        //TODO: we should add a resource id to the nav button!
        //click the nav button
        onView(allOf(withContentDescription("Navigate up"), isAssignableFrom(ImageButton.class))).perform(click());

        //click the profile tab
        onView(allOf(withId(R.id.nav_item), withText("PROFILE"))).perform(click());

        //wait for progress bar
        ViewUtils.waitForViewNotVisible(android.R.id.progress);

        //replace the phone number text
        onView(withId(R.id.phone_text)).perform(replaceText("9876543210"));

        onView(withId(R.id.old_password_text)).perform(click()).perform(typeText("password"));
        onView(withId(R.id.new_password_text)).perform(click()).perform(typeText("newpassword"));

        //press the update button
        onView(withId(R.id.update_button)).perform(click());

        //verify that we get a success message
        //TODO: how to check for toasts?
    }

}