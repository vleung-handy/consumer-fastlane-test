package com.handybook.handybook.tool.util;

import android.preference.PreferenceManager;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.Espresso;

import com.handybook.handybook.R;
import com.handybook.handybook.tool.model.TestUser;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.scrollTo;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.anyOf;

public class AppInteractionUtil {

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
     *
     * assumes we have just opened the app
     */
    public static void logOutAndPassOnboarding() {
        //log out if necessary
        //open nav drawer to log out if necessary

        // Skip 'share the love' if it shows up
        dismissShareTheLoveIfNeeded();

        // Skip the old onboarding screen if present
        if(ViewUtil.isViewDisplayed(R.id.start_button))
        {
            onView(withId(R.id.login_button)).check(matches(isDisplayed()));
            onView(withId(R.id.start_button)).perform(click());
        }

        //now we are either on the upcoming bookings page (if logged-in) or service categories page (if logged-out)
        //check if bottom nav is visible
        ViewUtil.waitForViewVisibility(anyOf(
                withId(R.id.fragment_service_categories_home_sign_in_text),
                withId(R.id.bottom_navigation)), true, ViewUtil.LONG_MAX_WAIT_TIME_MS);

        if(ViewUtil.isViewDisplayed(R.id.fragment_service_categories_home_sign_in_text))
        {
            //we are already signed out
            return;
        }
        //we are signed in

        //navigate to account page
        onView(withId(R.id.account)).perform(click());
        onView(withId(R.id.account_sign_out_button)).perform(scrollTo(), click());

        //pass log-out confirmation dialog
        onView(withText(R.string.account_sign_out)).perform(click());
        waitForServiceCategoriesPage();
    }

    /**
     * TODO: bypass login for non-login tests like in portal
     *
     * assumes that we are logged-out and on the service categories page
     * @param testUser
     */
    public static void logIn(TestUser testUser) {
        onView(withId(R.id.fragment_service_categories_home_sign_in_text)).perform(click());

        TextViewUtil.updateEditTextView(R.id.email_text, testUser.getEmail());
        TextViewUtil.updateEditTextView(R.id.password_text, testUser.getPassword());
        Espresso.closeSoftKeyboard();
        onView(withId(R.id.login_button)).perform(click());
    }

    public static void clickHomeCleaningServiceCategory()
    {
        onView(withText("Home Cleaning")).perform(click());
    }

    public static void clickHandymanServiceCategory()
    {
        onView(withText("Handyman")).perform(click());
    }

    public static void waitForServiceCategoriesPage() {
        ViewUtil.waitForViewVisibility(withText("Home Cleaning"), true, ViewUtil.LONG_MAX_WAIT_TIME_MS);
        //TODO do we also need to wait for the views inside it?
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
     * NOTE: make sure to do commit instead of apply, we need these to be 100% committed for the
     * tests to pass.
     */
    public static void clearSharedPrefs() {
        PreferenceManager.getDefaultSharedPreferences(InstrumentationRegistry.getTargetContext())
                         .edit().clear().commit();
    }

    private static void dismissShareTheLoveIfNeeded() {
        if (ViewUtil.isViewDisplayed(withText(R.string.referral_dialog_title))) {
            onView(withId(R.id.dialog_referral_close_button)).perform(click());
        }
    }
}
