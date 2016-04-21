package com.handybook.handybook.testutil;

import android.widget.ImageButton;

import com.handybook.handybook.R;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.matcher.ViewMatchers.isAssignableFrom;
import static android.support.test.espresso.matcher.ViewMatchers.withContentDescription;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.Matchers.allOf;

/**
 * utility class containing commonly used methods
 * that interact with app-specific UI items
 */
public class AppInteractionUtils
{
    /**
     * when the app first launches after an install
     * skip the extra screen that is displayed
     * that shows "log in" and "get started" buttons
     */
    public static void clickGetStartedButtonIfPresent()
    {
        try
        {
            onView(withId(R.id.start_button)).perform(click());
        }
        catch (Throwable e)
        {
            //do nothing
        }
    }

    /**
     * clicks the button on the top left that opens the navigation menu
     */
    public static void clickOpenNavigationMenuButton()
    {
        //click the nav button
        onView(allOf(withContentDescription("Navigate up"), isAssignableFrom(ImageButton.class))).
                perform(click());
    }

    public static void waitForProgressDialog()
    {
        ViewUtils.waitForViewToAppearThenDisappear(android.R.id.progress);
    }
}
