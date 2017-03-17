package com.handybook.handybook.test.booking;

import android.support.test.espresso.Espresso;

import com.handybook.handybook.R;
import com.handybook.handybook.core.ui.activity.SplashActivity;
import com.handybook.handybook.tool.data.TestUsers;
import com.handybook.handybook.tool.model.Address;
import com.handybook.handybook.tool.model.TestUser;
import com.handybook.handybook.tool.util.AppInteractionUtil;
import com.handybook.handybook.tool.util.LauncherActivityTestRule;
import com.handybook.handybook.tool.util.TextViewUtil;
import com.handybook.handybook.tool.util.ViewUtil;
import com.stripe.android.model.Card;

import org.junit.Rule;
import org.junit.Test;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.scrollTo;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

//note that animations should be disabled on the device running these tests
public class BookingCreationFirstTimeTest {

    @Rule
    public LauncherActivityTestRule<SplashActivity> mActivityRule =
            new LauncherActivityTestRule<>(SplashActivity.class);

    /**
     * basic test for ensuring that a new user can create a cleaning booking with default values
     */
    @Test
    public void testFirstTimeUserCanCreateCleaningBooking() {
        TestUser testUser = TestUsers.getUserRandomEmail();
        AppInteractionUtil.logOutAndPassOnboarding();

        //wait for network call to return with service list
        AppInteractionUtil.waitForServiceCategoriesPage();

        AppInteractionUtil.clickHomeCleaningServiceCategory();

        testGetQuoteFlow(testUser);

        //use default frequency
        ViewUtil.waitForTextVisible(R.string.how_often, ViewUtil.SHORT_MAX_WAIT_TIME_MS);
        clickNextButton();

        //use default extras
        clickNextButton();

        //fill out address fields
        Address address = testUser.getAddress();
        TextViewUtil.updateEditTextView(R.id.booking_address_fullname, testUser.getFullName());
        TextViewUtil.updateEditTextView(
                R.id.autocomplete_address_text_street,
                address.getStreetAddress1()
        );
        TextViewUtil.updateEditTextView(
                R.id.autocomplete_address_text_other,
                address.getStreetAddress2()
        );
        TextViewUtil.updateEditTextView(R.id.booking_address_phone, testUser.getPhoneNumber());
        clickNextButton();

        //fill out credit card fields
        ViewUtil.waitForViewVisible(R.id.credit_card_text, ViewUtil.LONG_MAX_WAIT_TIME_MS);
        Card card = testUser.getCard();
        TextViewUtil.updateEditTextView(R.id.credit_card_text, card.getNumber());
        TextViewUtil.updateEditTextView(R.id.exp_text, card.getExpMonth() + "" + card.getExpYear());
        TextViewUtil.updateEditTextView(R.id.cvc_text, card.getCVC());
        //click the complete button
        clickNextButton();

        /*post-confirmation pages*/

        //entry info page
        //wait for network
        ViewUtil.waitForTextVisible(R.string.confirmation, ViewUtil.LONG_MAX_WAIT_TIME_MS);
        clickNextButton();

        //preferences page
        ViewUtil.waitForTextVisible(R.string.cleaning_routine, ViewUtil.SHORT_MAX_WAIT_TIME_MS);
        clickNextButton();

        //enter password
        TextViewUtil.updateEditTextView(
                R.id.password_text,
                testUser.getPassword()
        );
        clickNextButton();

        //wait for booking details page
        ViewUtil.waitForViewVisible(R.id.booking_detail_view, ViewUtil.LONG_MAX_WAIT_TIME_MS);
    }

    private void testGetQuoteFlow(TestUser testUser)
    {
        try {
            //check if the consolidated flow is enabled
            ViewUtil.waitForViewVisible(R.id.fragment_booking_get_quote_container, ViewUtil.SHORT_MAX_WAIT_TIME_MS);
        }
        catch (Exception e)
        {
            //enter zip code
            ViewUtil.waitForViewVisible(R.id.zip_text, ViewUtil.SHORT_MAX_WAIT_TIME_MS);
            TextViewUtil.updateEditTextView(R.id.zip_text, testUser.getAddress().getZipCode());
            clickNextButton();

            //use default beds + baths
            //wait for network
            ViewUtil.waitForViewVisible(R.id.options_layout, ViewUtil.SHORT_MAX_WAIT_TIME_MS);
            clickNextButton();

            //use default date at 9 am
            AppInteractionUtil.inputBookingTime(9, 0);

            clickNextButton();

            //create a new user
            TextViewUtil.updateEditTextView(R.id.email_text, testUser.getEmail());
            onView(withId(R.id.login_button)).perform(click());
            return;
        }

        //test consolidated flow

        //enter zip code
        ViewUtil.waitForViewVisible(R.id.booking_zipcode_input_text, ViewUtil.SHORT_MAX_WAIT_TIME_MS);
        TextViewUtil.updateEditTextView(R.id.booking_zipcode_input_text, testUser.getAddress().getZipCode());

        //use default beds + baths
        ViewUtil.waitForViewVisible(R.id.booking_options_input, ViewUtil.SHORT_MAX_WAIT_TIME_MS);

        //use default date at 9 am
        onView(withId(R.id.booking_edit_time_button)).perform(scrollTo());
        AppInteractionUtil.inputBookingTime(9, 0);

        onView(withId(R.id.booking_email_input)).perform(scrollTo());
        TextViewUtil.updateEditTextView(R.id.booking_email_input, testUser.getEmail());

        onView(withId(R.id.fragment_booking_get_quote_next_button)).perform(scrollTo(), click());
    }

    private void clickNextButton() {
        Espresso.closeSoftKeyboard();
        onView(withId(R.id.next_button)).perform(click());
    }
}
