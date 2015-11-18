package com.handybook.handybook.testutil;

import android.widget.ImageButton;

import com.handybook.handybook.R;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isAssignableFrom;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withContentDescription;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.Matchers.allOf;

/**
 * utility class containing commonly used methods
 * that interact with app-specific UI items
 */
public class AppInteractionUtils
{
    public static void clickOpenNavigationMenuButton()
    {
        //click the nav button
        onView(allOf(withContentDescription("Navigate up"), isAssignableFrom(ImageButton.class))).
                perform(click());
    }

    public static void waitForHomeScreenAndNavMenuDance()
    {
        //wait for the nav menu to go away
        //TODO: why is the nav menu popping in and out? shouldn't be doing that
        ViewUtils.waitForViewToAppearThenDisappear(R.id.nav_fragment_container);

        //check that home screen is displayed
        onView(withId(R.id.category_layout)).check(matches(isDisplayed()));
    }

    public static void waitForProgressDialog()
    {
        ViewUtils.waitForViewToAppearThenDisappear(android.R.id.progress);
    }
}
