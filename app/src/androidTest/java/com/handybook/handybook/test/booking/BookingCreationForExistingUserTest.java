package com.handybook.handybook.test.booking;

import android.support.test.espresso.Espresso;
import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.runner.AndroidJUnit4;

import com.handybook.handybook.R;
import com.handybook.handybook.booking.ui.activity.ServiceCategoriesActivity;
import com.handybook.handybook.test.LauncherActivityTestRule;
import com.handybook.handybook.test.ViewMatchers;
import com.handybook.handybook.test.data.TestUsers;
import com.handybook.handybook.test.model.TestUser;
import com.handybook.handybook.test.util.AppInteractionUtil;
import com.handybook.handybook.test.util.ViewUtil;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

//note that animations should be disabled on the device running these tests
@RunWith(AndroidJUnit4.class)
public class BookingCreationForExistingUserTest {

    @Rule
    public LauncherActivityTestRule<ServiceCategoriesActivity> mActivityRule =
            new LauncherActivityTestRule<>(ServiceCategoriesActivity.class);

    /**
     * basic test for ensuring that an existing user can create a cleaning booking with default values
     */
    @Test
    public void testExistingUserCanCreateCleaningBooking() {
        TestUser testUser = TestUsers.EXISTING_USER_BOOKING_CREATION;
        AppInteractionUtil.logOutAndPassOnboarding();
        AppInteractionUtil.logIn(testUser);
        AppInteractionUtil.waitForServiceCategoriesPage();

        //create a home cleaning - assuming that is at position 0
        //(don't know how to cleanly query nested item)
        onView(withId(R.id.recycler_view)).perform(
                RecyclerViewActions.actionOnItemAtPosition(0, AppInteractionUtil.recyclerClick()));

        //enter zip code
        ViewUtil.waitForViewVisible(R.id.zip_text, ViewUtil.SHORT_MAX_WAIT_TIME_MS);
        clickNextButton();

        //use default beds + baths
        //wait for network
        ViewUtil.waitForViewVisible(R.id.options_layout, ViewUtil.SHORT_MAX_WAIT_TIME_MS);
        clickNextButton();

        //use default date at 9 am
        AppInteractionUtil.inputBookingTime(9, 0);
        clickNextButton();

        //use default frequency
        ViewUtil.waitForTextVisible(R.string.how_often, ViewUtil.SHORT_MAX_WAIT_TIME_MS);
        clickNextButton();

        //use default extras, check for peak pricing
        if (ViewUtil.isViewDisplayed(withText(R.string.peak_price_info))) {
            // Skip it if possible
            if (ViewUtil.isViewDisplayed(withId(R.id.skip_button))) {
                onView(withId(R.id.skip_button)).perform(click());
            }
            else {
                // Click on first time on the next day
                onView(withId(R.id.arrow_right)).perform(click());
                ViewMatchers.childAtIndex(withId(R.id.time_text), 0);
            }
        }
        clickNextButton();

        //use previous address
        ViewUtil.waitForViewVisible(
                R.id.autocomplete_address_text_street,
                ViewUtil.SHORT_MAX_WAIT_TIME_MS
        );
        Espresso.closeSoftKeyboard();
        clickNextButton();

        //use previous credit card
        ViewUtil.waitForViewVisible(
                R.id.payment_fragment_credit_card_info_container,
                ViewUtil.SHORT_MAX_WAIT_TIME_MS
        );
        Espresso.closeSoftKeyboard();
        clickNextButton();

        /*post-confirmation pages*/

        //entry info page
        //wait for network
        ViewUtil.waitForTextVisible(R.string.confirmation, ViewUtil.LONG_MAX_WAIT_TIME_MS);
        clickNextButton();

        //preferences page
        ViewUtil.waitForTextVisible(R.string.cleaning_routine, ViewUtil.SHORT_MAX_WAIT_TIME_MS);
        clickNextButton();

        //wait for booking details page
        ViewUtil.waitForViewVisible(R.id.booking_detail_view, ViewUtil.LONG_MAX_WAIT_TIME_MS);
    }

    private void clickNextButton() {
        onView(withId(R.id.next_button)).perform(click());
    }
}
