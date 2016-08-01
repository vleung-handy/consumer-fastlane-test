package com.handybook.handybook.test.util;

import android.support.test.espresso.contrib.DrawerActions;

import com.handybook.handybook.R;
import com.handybook.handybook.test.model.TestUser;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

public class AppInteractionUtil
{
    /**
     * logs out if necessary and passes the onboarding screen if necessary
     *
     * need to do this because device data isn't cleared after each test run
     *
     * TODO: bypass login for non-login tests like in portal
     */
    public static void logOutAndPassOnboarding()
    {
        //log out if necessary
        //open nav drawer
        DrawerActions.openDrawer(R.id.drawer_layout);

        //log out
        if(ViewUtil.isViewDisplayed(withText(R.string.log_out)))
        {
            //press the log out button in the nav drawer
            onView(withText(R.string.log_out)).perform(click());

            //press the log out button in the confirmation dialog
            onView(withText(R.string.log_out)).perform(click());
            ViewUtil.waitForTextNotVisible(R.string.log_out, ViewUtil.SHORT_MAX_WAIT_TIME_MS);
            //drawer will be closed
        }
        else
        {
            //close the drawer
            DrawerActions.closeDrawer(R.id.drawer_layout);
        }

        if(ViewUtil.isViewDisplayed(R.id.start_button))
        {
            onView(withId(R.id.start_button)).perform(click());
        }
    }

    /**
     * TODO: bypass login for non-login tests like in portal
     * @param testUser
     */
    public static void logIn(TestUser testUser)
    {
        DrawerActions.openDrawer(R.id.drawer_layout);
        onView(withText(R.string.log_in)).perform(click());
        TextViewUtil.updateEditTextView(R.id.email_text, testUser.getEmail());
        TextViewUtil.updateEditTextView(R.id.password_text, testUser.getPassword());
        onView(withId(R.id.login_button)).perform(click());
    }
}
