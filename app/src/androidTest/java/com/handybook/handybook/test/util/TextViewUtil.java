package com.handybook.handybook.test.util;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.replaceText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

/**
 * utility class containing non-app-specific methods to check for certain TextView states
 */
public class TextViewUtil
{
    public static void updateEditTextView(int viewResourceId, String newText)
    {
        onView(withId(viewResourceId)).perform(click(), replaceText(newText), closeSoftKeyboard());
    }

    public static void assertViewHasText(int viewResourceId, String expectedText)
    {
        onView(withId(viewResourceId)).check(matches(withText(expectedText)));
    }
}
