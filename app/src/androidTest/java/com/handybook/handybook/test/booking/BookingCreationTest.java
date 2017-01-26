package com.handybook.handybook.test.booking;

import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.runner.AndroidJUnit4;
import android.view.View;

import com.handybook.handybook.R;
import com.handybook.handybook.booking.ui.activity.ServiceCategoriesActivity;
import com.handybook.handybook.test.LauncherActivityTestRule;
import com.handybook.handybook.test.ViewMatchers;
import com.handybook.handybook.test.data.TestUsers;
import com.handybook.handybook.test.model.Address;
import com.handybook.handybook.test.model.TestUser;
import com.handybook.handybook.test.util.AppInteractionUtil;
import com.handybook.handybook.test.util.TextViewUtil;
import com.handybook.handybook.test.util.ViewUtil;
import com.stripe.android.model.Card;

import org.hamcrest.Matcher;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.scrollTo;
import static android.support.test.espresso.action.ViewActions.swipeLeft;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.contrib.PickerActions.setTime;
import static android.support.test.espresso.matcher.ViewMatchers.withChild;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withParent;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;

//note that animations should be disabled on the device running these tests
@RunWith(AndroidJUnit4.class)
public class BookingCreationTest
{
    @Rule
    public LauncherActivityTestRule<ServiceCategoriesActivity> mActivityRule =
            new LauncherActivityTestRule<>(ServiceCategoriesActivity.class);

    /**
     * basic test for ensuring that a new user can create a cleaning booking with default values
     */
    @Test
    public void testFirstTimeUserCanCreateCleaningBooking()
    {
        TestUser testUser = TestUsers.FIRST_TIME_USER_BOOKING_CREATION;
        AppInteractionUtil.logOutAndPassOnboarding();

        //wait for network call to return with service list
        AppInteractionUtil.waitForServiceCategoriesPage();

        //create a home cleaning - assuming that is at position 0
        //(don't know how to cleanly query nested item)
        onView(withId(R.id.recycler_view)).perform(
                RecyclerViewActions.actionOnItemAtPosition(0, click()));

        //enter zip code
        ViewUtil.waitForViewVisible(R.id.zip_text, ViewUtil.SHORT_MAX_WAIT_TIME_MS);
        TextViewUtil.updateEditTextView(R.id.zip_text, testUser.getAddress().getZipCode());
        clickNextButton();

        //use default beds + baths
        //wait for network
        ViewUtil.waitForViewVisible(R.id.options_layout, ViewUtil.SHORT_MAX_WAIT_TIME_MS);
        clickNextButton();

        //use default date at 9 am
        ViewUtil.waitForViewVisible(R.id.time_picker, ViewUtil.LONG_MAX_WAIT_TIME_MS);
        onView(withId(R.id.time_picker)).perform(setTime(9, 0));
        clickNextButton();

        //create a new user
        TextViewUtil.updateEditTextView(R.id.email_text, testUser.getEmail());
        onView(withId(R.id.login_button)).perform(click());

        //use default frequency
        ViewUtil.waitForTextVisible(R.string.how_often, ViewUtil.SHORT_MAX_WAIT_TIME_MS);
        clickNextButton();

        //use default extras
        clickNextButton();

        //fill out address fields
        Address address = testUser.getAddress();
        TextViewUtil.updateEditTextView(R.id.text_fullname, testUser.getFullName());
        TextViewUtil.updateEditTextView(
                R.id.autocomplete_address_text_street,
                address.getStreetAddress1()
        );
        TextViewUtil.updateEditTextView(
                R.id.autocomplete_address_text_other,
                address.getStreetAddress2()
        );
        TextViewUtil.updateEditTextView(R.id.text_phone, testUser.getPhoneNumber());
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

    /**
     * basic test for ensuring that an existing user can create a cleaning booking with default values
     */
    @Test
    public void testExistingUserCanCreateCleaningBooking()
    {
        TestUser testUser = TestUsers.EXISTING_USER_BOOKING_CREATION;
        AppInteractionUtil.logOutAndPassOnboarding();
        AppInteractionUtil.logIn(testUser);
        AppInteractionUtil.waitForServiceCategoriesPage();

        //create a home cleaning - assuming that is at position 0
        //(don't know how to cleanly query nested item)
        onView(withId(R.id.recycler_view)).perform(
                RecyclerViewActions.actionOnItemAtPosition(0, click()));

        //enter zip code
        ViewUtil.waitForViewVisible(R.id.zip_text, ViewUtil.SHORT_MAX_WAIT_TIME_MS);
        clickNextButton();

        //use default beds + baths
        //wait for network
        ViewUtil.waitForViewVisible(R.id.options_layout, ViewUtil.SHORT_MAX_WAIT_TIME_MS);
        clickNextButton();

        //use default date at 9 am
        ViewUtil.waitForViewVisible(R.id.date_picker, ViewUtil.SHORT_MAX_WAIT_TIME_MS);
        onView(withId(R.id.time_picker)).perform(setTime(9, 0));
        clickNextButton();

        //use default frequency
        ViewUtil.waitForTextVisible(R.string.how_often, ViewUtil.SHORT_MAX_WAIT_TIME_MS);
        clickNextButton();

        //use default extras, check for peak pricing
        if (ViewUtil.isViewDisplayed(withText(R.string.peak_price_info)))
        {
            // Skip it if possible
            if (ViewUtil.isViewDisplayed(withId(R.id.skip_button)))
            {
                onView(withId(R.id.skip_button)).perform(click());
            }
            else
            {
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

    /**
     * basic test for ensuring that an existing user can create a handyman booking with default values
     */
    @Test
    public void testExistingUserCanCreateHandymanBooking()
    {
        TestUser testUser = TestUsers.EXISTING_USER_BOOKING_CREATION;
        AppInteractionUtil.logOutAndPassOnboarding();
        AppInteractionUtil.logIn(testUser);
        AppInteractionUtil.waitForServiceCategoriesPage();

        //select the handyman service
        Matcher<View> handymanRecyclerViewItemMatcher =
                withChild(withChild(withChild(withChild(withText("Handyman")))));
        onView(withId(R.id.recycler_view)).perform(
                RecyclerViewActions.actionOnItem(handymanRecyclerViewItemMatcher, click()));

        //select the hanging items service
        Matcher<View> matchingItemsMatcher = withText("Hanging items");
        ViewUtil.waitForViewVisibility(matchingItemsMatcher, true, ViewUtil.SHORT_MAX_WAIT_TIME_MS);
        onView(matchingItemsMatcher).perform(click());

        //enter zip code
        ViewUtil.waitForViewVisible(R.id.zip_text, ViewUtil.SHORT_MAX_WAIT_TIME_MS);
        clickNextButton();

        //wait for network and select blinds option
        ViewUtil.waitForViewVisible(R.id.options_layout, ViewUtil.SHORT_MAX_WAIT_TIME_MS);
        Matcher<View> blindsOptionsSpinnerMatcher = allOf(
                withId(R.id.options_spinner),
                withParent(withParent(withParent(
                        withChild(withText("Blinds")))))
        );
        onView(blindsOptionsSpinnerMatcher).perform(scrollTo(), swipeLeft());
        clickNextButton();

        //wait for network
        ViewUtil.waitForViewVisibility(
                withText(R.string.comments),
                true,
                ViewUtil.SHORT_MAX_WAIT_TIME_MS
        );
        onView(withId(R.id.edit_text)).perform(typeText("blah blah"));
        clickNextButton();

        //select time to be 0700
        ViewUtil.waitForViewVisible(R.id.date_picker, ViewUtil.SHORT_MAX_WAIT_TIME_MS);
        onView(withId(R.id.time_picker)).perform(setTime(7, 0));
        clickNextButton();

        //use previous address
        ViewUtil.waitForViewVisible(
                R.id.autocomplete_address_text_street,
                ViewUtil.SHORT_MAX_WAIT_TIME_MS
        );
        clickNextButton();

        //use previous credit card
        ViewUtil.waitForViewVisible(
                R.id.next_button,
                ViewUtil.SHORT_MAX_WAIT_TIME_MS
        );
        clickNextButton();

        /*post-confirmation pages*/

        //entry info page
        //wait for network
        ViewUtil.waitForTextVisible(R.string.confirmation, ViewUtil.LONG_MAX_WAIT_TIME_MS);
        clickNextButton();

        //preferences page
        ViewUtil.waitForViewVisible(R.id.preferences_note_to_pro, ViewUtil.SHORT_MAX_WAIT_TIME_MS);
        clickNextButton();

        //wait for booking details page
        ViewUtil.waitForViewVisible(R.id.booking_detail_view, ViewUtil.LONG_MAX_WAIT_TIME_MS);
    }

    private void clickNextButton()
    {
        onView(withId(R.id.next_button)).perform(click());
    }
}
