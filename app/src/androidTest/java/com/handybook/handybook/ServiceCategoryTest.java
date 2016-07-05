package com.handybook.handybook;

import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;

import com.handybook.handybook.booking.ui.activity.ServiceCategoriesActivity;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class ServiceCategoryTest
{

    @Rule
    public ActivityTestRule<ServiceCategoriesActivity> mActivityRule = new ActivityTestRule<>(
            ServiceCategoriesActivity.class);

    /**
     * Tests that the 5 categories have the correct names.
     * <p>
     * Will only work if there isn't a ratings dialog overlay
     */
    @Test
    public void testCategoryNames()
    {
        onView(allOf(withId(R.id.title), withText("Home Cleaner")))
                .check(matches(withText("Home Cleaner")));

        onView(allOf(withId(R.id.title), withText("Handyman")))
                .check(matches(withText("Handyman")));

        onView(allOf(withId(R.id.title), withText("Plumber")))
                .check(matches(withText("Plumber")));

        onView(allOf(withId(R.id.title), withText("Electrician")))
                .check(matches(withText("Electrician")));

        onView(allOf(withId(R.id.title), withText("Interior Painter")))
                .check(matches(withText("Interior Painter")));
    }
}
