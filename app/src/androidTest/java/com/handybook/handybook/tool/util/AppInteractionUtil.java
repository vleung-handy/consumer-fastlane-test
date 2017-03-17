package com.handybook.handybook.tool.util;

import android.preference.PreferenceManager;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.Espresso;
import android.support.test.espresso.UiController;
import android.support.test.espresso.ViewAction;
import android.support.test.espresso.contrib.DrawerActions;
import android.support.test.espresso.contrib.NavigationViewActions;
import android.view.View;

import com.handybook.handybook.R;
import com.handybook.handybook.tool.model.TestUser;

import org.hamcrest.Matcher;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.scrollTo;
import static android.support.test.espresso.action.ViewActions.swipeUp;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.any;

public class AppInteractionUtil {

    /**
     * For our services home page, even though the animations are turned off, for some reason sometimes
     * they still animate. So this will click the first item
     * @return
     */
    public static ViewAction recyclerClick() {
        return new ViewAction() {

            @Override
            public Matcher<View> getConstraints() {
                return any(View.class);
            }

            @Override
            public String getDescription() {
                return "performing click() on recycler view item";
            }

            @Override
            public void perform(UiController uiController, View view) {
                view.performClick();
            }
        };
    }

    public static void inputBookingTime(int hourOfDay, int minuteOfHour)
    {
        ViewUtil.waitForViewVisible(
                R.id.booking_edit_time_button,
                ViewUtil.SHORT_MAX_WAIT_TIME_MS
        );
        onView(withId(R.id.booking_edit_time_button)).perform(scrollTo(), click());
        onView(withId(R.id.fragment_dialog_booking_time_input_picker)).perform(
                BookingTimePickerActions.setTime(hourOfDay, minuteOfHour));
        onView(withId(R.id.fragment_dialog_booking_time_input_save_button)).perform(click());
    }

    /**
     * logs out if necessary and passes the onboarding screen if necessary
     * <p>
     * need to do this because device data isn't cleared after each test run
     * <p>
     * TODO: bypass login for non-login tests like in portal
     */
    public static void logOutAndPassOnboarding() {
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
         */ {
            openDrawer();

            //log out
            onView(withId(android.R.id.content)).perform(swipeUp());
            if (!ViewUtil.isViewDisplayed(withText(R.string.sign_in))) {
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
            else {
                //close the drawer
                closeDrawer();
            }
        }

        if (ViewUtil.isViewDisplayed(R.id.start_button)) {
            onView(withId(R.id.start_button)).perform(click());
        }
    }

    /**
     * TODO: bypass login for non-login tests like in portal
     *
     * @param testUser
     */
    public static void logIn(TestUser testUser) {
        openDrawer();

        //can no longer navigate the menu drawer by looking for "Sign In" text, as there are
        //multiple views with that string. Need to use NavigationViewActions instead.
        onView(withId(R.id.navigation)).perform(NavigationViewActions.navigateTo(R.id.nav_menu_log_in));

        TextViewUtil.updateEditTextView(R.id.email_text, testUser.getEmail());
        TextViewUtil.updateEditTextView(R.id.password_text, testUser.getPassword());
        Espresso.closeSoftKeyboard();
        onView(withId(R.id.login_button)).perform(click());
    }

    public static void waitForServiceCategoriesPage() {
        //would rather wait for service recyclerview but it's flaky
        ViewUtil.waitForViewVisible(R.id.recycler_view, ViewUtil.LONG_MAX_WAIT_TIME_MS);
    }

    public static void waitForOnboardZipPage() {
        ViewUtil.waitForViewVisible(R.id.onboard_edit_zip, ViewUtil.LONG_MAX_WAIT_TIME_MS);
    }

    public static void waitForOnboardEmailPage() {
        ViewUtil.waitForViewVisible(R.id.onboard_edit_email, ViewUtil.LONG_MAX_WAIT_TIME_MS);
    }

    public static void waitForBottomNavPage() {
        ViewUtil.waitForViewVisible(R.id.activity_bottom_nav, ViewUtil.LONG_MAX_WAIT_TIME_MS);
    }

    public static void pauseTestFor(long milliseconds) {
        try {
            Thread.sleep(milliseconds);
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * Open drawer
     */
    public static void openDrawer() {
        ViewUtil.waitForViewVisible(R.id.drawer_layout, ViewUtil.LONG_MAX_WAIT_TIME_MS);
        onView(withId(R.id.drawer_layout)).perform(DrawerActions.open());
        dismissShareTheLoveIfNeeded();
    }

    /**
     * NOTE: make sure to do commit instead of apply, we need these to be 100% committed for the
     * tests to pass.
     */
    public static void clearSharedPrefs() {
        PreferenceManager.getDefaultSharedPreferences(InstrumentationRegistry.getTargetContext())
                         .edit().clear().commit();
    }

    /**
     * Close drawer
     */
    public static void closeDrawer() {
        ViewUtil.waitForViewVisible(R.id.drawer_layout, ViewUtil.LONG_MAX_WAIT_TIME_MS);
        onView(withId(R.id.drawer_layout)).perform(DrawerActions.close());
        dismissShareTheLoveIfNeeded();
    }

    private static void dismissShareTheLoveIfNeeded() {
        if (ViewUtil.isViewDisplayed(withText(R.string.referral_dialog_title))) {
            onView(withId(R.id.dialog_referral_close_button)).perform(click());
        }
    }
}
