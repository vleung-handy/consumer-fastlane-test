package com.handybook.handybook;

import android.support.test.espresso.contrib.DrawerActions;
import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.handybook.handybook.booking.ui.activity.ServiceCategoriesActivity;
import com.handybook.handybook.test.data.TestUsers;
import com.handybook.handybook.test.model.Address;
import com.handybook.handybook.test.model.TestUser;
import com.handybook.handybook.test.util.TextViewUtil;
import com.handybook.handybook.test.util.ViewUtil;
import com.stripe.android.model.Card;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

//note that animations should be disabled on the device running these tests
@RunWith(AndroidJUnit4.class)
public class BookingCreationTest
{
    private static final TestUser TEST_USER = TestUsers.FIRST_TIME_USER_BOOKING_CREATION;

    @Rule
    public ActivityTestRule<ServiceCategoriesActivity> mActivityRule =
            new ActivityTestRule<>(ServiceCategoriesActivity.class);

    /**
     * assumes no one is logged in
     *
     * basic test for ensuring that a new user can create a booking with default values
     */
    @Test
    public void testFirstTimeUserCanCreateBooking()
    {
        logOutAndPassOnboarding();

        //wait for network call to return with service list
        ViewUtil.waitForViewVisible(R.id.recycler_view, ViewUtil.LONG_MAX_WAIT_TIME_MS);

        //create a home cleaning - assuming that is at position 0
        //(don't know how to cleanly query nested item)
        onView(withId(R.id.recycler_view)).perform(
                RecyclerViewActions.actionOnItemAtPosition(0, click()));

        //enter zip code
        ViewUtil.waitForViewVisible(R.id.zip_text, ViewUtil.SHORT_MAX_WAIT_TIME_MS);
        TextViewUtil.updateEditTextView(R.id.zip_text, TEST_USER.getAddress().getZipCode());
        clickNextButton();

        //use default beds + baths
        //wait for network
        ViewUtil.waitForViewVisible(R.id.options_layout, ViewUtil.SHORT_MAX_WAIT_TIME_MS);
        clickNextButton();

        //use default time + date
        clickNextButton();

        //create a new user
        TextViewUtil.updateEditTextView(R.id.email_text, TEST_USER.getEmail());
        onView(withId(R.id.login_button)).perform(click());

        //use default frequency
        ViewUtil.waitForViewVisible(R.id.next_button, ViewUtil.SHORT_MAX_WAIT_TIME_MS);
        clickNextButton();

        //use default extras
        clickNextButton();

        //fill out address fields
        Address address = TEST_USER.getAddress();
        TextViewUtil.updateEditTextView(R.id.fullname_text, TEST_USER.getFullName());
        TextViewUtil.updateEditTextView(R.id.street_addr_text, address.getStreetAddress1());
        TextViewUtil.updateEditTextView(R.id.other_addr_text, address.getStreetAddress2());
        TextViewUtil.updateEditTextView(R.id.phone_text, TEST_USER.getPhoneNumber());
        clickNextButton();

        //fill out credit card fields
        Card card = TEST_USER.getCard();
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
        TextViewUtil.updateEditTextView(R.id.password_text,
                TEST_USER.getPassword());
        clickNextButton();

        //wait for booking details page
        ViewUtil.waitForViewVisible(R.id.booking_detail_view, ViewUtil.LONG_MAX_WAIT_TIME_MS);
    }

    /**
     * logs out if necessary and passes the onboarding screen if necessary
     *
     * need to do this because device data isn't cleared after each test run
     */
    private void logOutAndPassOnboarding()
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
    
    private void clickNextButton()
    {
        onView(withId(R.id.next_button)).perform(click());
    }
}
