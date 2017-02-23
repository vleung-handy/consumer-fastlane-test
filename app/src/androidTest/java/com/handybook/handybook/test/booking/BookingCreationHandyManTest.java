package com.handybook.handybook.test.booking;

import android.support.test.espresso.Espresso;
import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.runner.AndroidJUnit4;
import android.view.View;

import com.handybook.handybook.R;
import com.handybook.handybook.booking.ui.activity.ServiceCategoriesActivity;
import com.handybook.handybook.test.LauncherActivityTestRule;
import com.handybook.handybook.test.data.TestUsers;
import com.handybook.handybook.test.model.TestUser;
import com.handybook.handybook.test.util.AppInteractionUtil;
import com.handybook.handybook.test.util.ViewUtil;

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
public class BookingCreationHandyManTest
{
    @Rule
    public LauncherActivityTestRule<ServiceCategoriesActivity> mActivityRule =
            new LauncherActivityTestRule<>(ServiceCategoriesActivity.class);

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
                RecyclerViewActions.actionOnItem(handymanRecyclerViewItemMatcher, AppInteractionUtil.recyclerClick()));

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
        Espresso.closeSoftKeyboard();
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
        Espresso.closeSoftKeyboard();
        clickNextButton();

        //use previous credit card
        ViewUtil.waitForViewVisible(
                R.id.payment_fragment_terms_of_use_text,
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
        ViewUtil.waitForViewVisible(R.id.preferences_note_to_pro, ViewUtil.SHORT_MAX_WAIT_TIME_MS);
        clickNextButton();

        //wait for booking details page
        ViewUtil.waitForViewVisible(R.id.booking_detail_view, ViewUtil.LONG_MAX_WAIT_TIME_MS);
    }

    private void clickNextButton()
    {
        Espresso.closeSoftKeyboard();
        onView(withId(R.id.next_button)).perform(click());
    }
}
