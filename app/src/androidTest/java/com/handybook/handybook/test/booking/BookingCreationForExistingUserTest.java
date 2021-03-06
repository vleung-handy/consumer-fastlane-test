package com.handybook.handybook.test.booking;

import android.support.test.espresso.Espresso;

import com.handybook.handybook.R;
import com.handybook.handybook.core.ui.activity.SplashActivity;
import com.handybook.handybook.tool.data.TestUsers;
import com.handybook.handybook.tool.model.TestUser;
import com.handybook.handybook.tool.util.AppInteractionUtil;
import com.handybook.handybook.tool.util.LauncherActivityTestRule;
import com.handybook.handybook.tool.util.TextViewUtil;
import com.handybook.handybook.tool.util.ViewMatchers;
import com.handybook.handybook.tool.util.ViewUtil;

import org.junit.Rule;
import org.junit.Test;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.scrollTo;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

//note that animations should be disabled on the device running these tests
public class BookingCreationForExistingUserTest {

    @Rule
    public LauncherActivityTestRule<SplashActivity> mActivityRule =
            new LauncherActivityTestRule<>(SplashActivity.class);

    /**
     * basic test for ensuring that an existing user can create a cleaning booking with default values
     */
    @Test
    public void testExistingUserCanCreateCleaningBooking() {
        TestUser testUser = TestUsers.EXISTING_USER_BOOKING_CREATION;
        AppInteractionUtil.logOutAndPassOnboarding();
        AppInteractionUtil.logIn(testUser);
        AppInteractionUtil.waitForServiceCategoriesPage();

        AppInteractionUtil.clickHomeCleaningServiceCategory();

        testGetQuoteFlow(testUser);

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
        clickNextButton();

        //use previous credit card
        ViewUtil.waitForViewVisible(
                R.id.payment_fragment_credit_card_info_container,
                ViewUtil.SHORT_MAX_WAIT_TIME_MS
        );
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

    private void testGetQuoteFlow(TestUser testUser) {
        try {
            //check if the consolidated flow is enabled
            ViewUtil.waitForViewVisible(
                    R.id.fragment_booking_get_quote_container,
                    ViewUtil.SHORT_MAX_WAIT_TIME_MS
            );
        }
        catch (Exception e) {
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
            return;
        }

        //test consolidated flow

        //enter zip code if necessary
        try {
            ViewUtil.waitForViewVisible(
                    R.id.booking_zipcode_input_text, ViewUtil.SHORT_MAX_WAIT_TIME_MS);
            TextViewUtil.updateEditTextView(
                    R.id.booking_zipcode_input_text, testUser.getAddress().getZipCode());
        }
        catch (Exception e) {}

        //use default beds + baths
        ViewUtil.waitForViewVisible(R.id.booking_options_input, ViewUtil.SHORT_MAX_WAIT_TIME_MS);

        //use default date at 9 am
        onView(withId(R.id.booking_edit_time_button)).perform(scrollTo());
        AppInteractionUtil.inputBookingTime(9, 0);

        onView(withId(R.id.fragment_booking_get_quote_next_button)).perform(scrollTo(), click());
    }

    private void clickNextButton() {
        Espresso.closeSoftKeyboard();
        onView(withId(R.id.next_button)).perform(click());
    }
}
