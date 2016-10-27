package com.handybook.handybook.test.util;

import android.support.test.espresso.contrib.DrawerActions;

import com.handybook.handybook.R;
import com.handybook.handybook.test.model.TestUser;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.scrollTo;
import static android.support.test.espresso.action.ViewActions.swipeUp;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

public class AppInteractionUtil
{
    /**
     * logs out if necessary and passes the onboarding screen if necessary
     * <p>
     * need to do this because device data isn't cleared after each test run
     * <p>
     * TODO: bypass login for non-login tests like in portal
     */
    public static void logOutAndPassOnboarding()
    {
        //log out if necessary
        //open nav drawer to log out if necessary

        // Skip 'share the love' if it shows up
        dismissShareTheLoveIfNeeded();

        if (!ViewUtil.isViewDisplayed(R.id.login_button))
        /**
         don't want to open and close drawer in onboarding activity
         because that causes a weird issue on Android 5.0 emulator in which
         app gets stuck on the progress dialog after click "get started"
         (this does not happen on an Android 6.0 device)

         TODO see if this happens on the actual cloud test devices
         */
        {
            openDrawer();

            //log out
            onView(withId(android.R.id.content)).perform(swipeUp());
            if (!ViewUtil.isViewDisplayed(withText(R.string.log_in)))
            {
                //press the my account button in the nav drawer
                onView(withText(R.string.account)).perform(click());

                ViewUtil.waitForViewInScrollViewVisible(
                        R.id.account_sign_out_button,
                        ViewUtil.LONG_MAX_WAIT_TIME_MS
                );
                onView(withId(R.id.account_sign_out_button)).perform(scrollTo()).perform(click());

                //press the log out button in the confirmation dialog
                onView(withText(R.string.account_sign_out)).perform(click());
                ViewUtil.waitForTextNotVisible(R.string.log_out, ViewUtil.SHORT_MAX_WAIT_TIME_MS);
                //drawer will be closed
            }
            else
            {
                //close the drawer
                closeDrawer();
            }
        }

        if (ViewUtil.isViewDisplayed(R.id.start_button))
        {
            onView(withId(R.id.start_button)).perform(click());
        }
    }

    /**
     * TODO: bypass login for non-login tests like in portal
     *
     * @param testUser
     */
    public static void logIn(TestUser testUser)
    {
        openDrawer();
        onView(withText(R.string.log_in)).perform(click());
        TextViewUtil.updateEditTextView(R.id.email_text, testUser.getEmail());
        TextViewUtil.updateEditTextView(R.id.password_text, testUser.getPassword());
        onView(withId(R.id.login_button)).perform(click());
    }

    public static void waitForServiceCategoriesPage()
    {
        //would rather wait for service recyclerview but it's flaky
        ViewUtil.waitForViewVisible(R.id.recycler_view, ViewUtil.LONG_MAX_WAIT_TIME_MS);
    }

    /**
     * Open drawer
     */
    public static void openDrawer()
    {
        ViewUtil.waitForViewVisible(R.id.drawer_layout, ViewUtil.LONG_MAX_WAIT_TIME_MS);
        onView(withId(R.id.drawer_layout)).perform(DrawerActions.open());
        dismissShareTheLoveIfNeeded();
    }

    /**
     * Close drawer
     */
    public static void closeDrawer()
    {
        ViewUtil.waitForViewVisible(R.id.drawer_layout, ViewUtil.LONG_MAX_WAIT_TIME_MS);
        onView(withId(R.id.drawer_layout)).perform(DrawerActions.close());
        dismissShareTheLoveIfNeeded();
    }

    private static void dismissShareTheLoveIfNeeded()
    {
        if (ViewUtil.isViewDisplayed(withText(R.string.referral_dialog_title)))
        {
            onView(withId(R.id.dialog_referral_close_button)).perform(click());
        }
    }
}
