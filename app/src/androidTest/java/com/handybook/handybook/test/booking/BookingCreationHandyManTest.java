package com.handybook.handybook.test.booking;

import android.support.test.espresso.Espresso;
import android.view.View;

import com.handybook.handybook.R;
import com.handybook.handybook.core.ui.activity.SplashActivity;
import com.handybook.handybook.tool.data.TestUsers;
import com.handybook.handybook.tool.model.TestUser;
import com.handybook.handybook.tool.util.AppInteractionUtil;
import com.handybook.handybook.tool.util.LauncherActivityTestRule;
import com.handybook.handybook.tool.util.TextViewUtil;
import com.handybook.handybook.tool.util.ViewUtil;

import org.hamcrest.Matcher;
import org.junit.Rule;
import org.junit.Test;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.scrollTo;
import static android.support.test.espresso.action.ViewActions.swipeLeft;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.matcher.ViewMatchers.withChild;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withParent;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;

//note that animations should be disabled on the device running these tests
public class BookingCreationHandyManTest {

    @Rule
    public LauncherActivityTestRule<SplashActivity> mActivityRule =
            new LauncherActivityTestRule<>(SplashActivity.class);

    /**
     * basic test for ensuring that an existing user can create a handyman booking with default values
     */
    @Test
    public void testExistingUserCanCreateHandymanBooking() {
        TestUser testUser = TestUsers.EXISTING_USER_BOOKING_CREATION;
        AppInteractionUtil.logOutAndPassOnboarding();
        AppInteractionUtil.logIn(testUser);
        AppInteractionUtil.waitForServiceCategoriesPage();

        AppInteractionUtil.clickHandymanServiceCategory();

        //select the hanging items service
        Matcher<View> matchingItemsMatcher = withText("Hanging items");
        ViewUtil.waitForViewVisibility(matchingItemsMatcher, true, ViewUtil.SHORT_MAX_WAIT_TIME_MS);
        onView(matchingItemsMatcher).perform(click());

        testGetQuoteFlow(testUser);

        //use previous address
        ViewUtil.waitForViewVisible(
                R.id.autocomplete_address_text_street,
                ViewUtil.SHORT_MAX_WAIT_TIME_MS
        );
        clickNextButton();

        //use previous credit card
        ViewUtil.waitForViewVisible(
                R.id.payment_fragment_terms_of_use_text,
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

    private void testGetQuoteFlow(final TestUser testUser) {
        try {
            //check if the consolidated flow is enabled. If this scully experiment is not enabled
            // Do it previously
            ViewUtil.waitForViewVisible(
                    R.id.fragment_booking_get_quote_container,
                    ViewUtil.SHORT_MAX_WAIT_TIME_MS
            );
        }
        catch (Exception e) {
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
            AppInteractionUtil.inputBookingTime(7, 0);

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

        //select blinds
        onView(withText("Blinds")).perform(scrollTo());
        ViewUtil.waitForViewVisible(R.id.booking_options_input, ViewUtil.SHORT_MAX_WAIT_TIME_MS);
        Matcher<View> blindsOptionsSpinnerMatcher = allOf(
                withId(R.id.options_spinner),
                withParent(withParent(withParent(
                        withChild(withText("Blinds")))))
        );
        onView(blindsOptionsSpinnerMatcher).perform(scrollTo(), swipeLeft());

        //fill out comments
        onView(withId(R.id.edit_text)).perform(scrollTo());
        TextViewUtil.updateEditTextView(R.id.edit_text, "blah blah");
        Espresso.closeSoftKeyboard();

        //select time to be 0700
        onView(withId(R.id.booking_edit_time_button)).perform(scrollTo());
        AppInteractionUtil.inputBookingTime(7, 0);

        onView(withId(R.id.fragment_booking_get_quote_next_button)).perform(scrollTo(), click());
    }

    private void clickNextButton() {
        Espresso.closeSoftKeyboard();
        onView(withId(R.id.next_button)).perform(click());
    }
}
