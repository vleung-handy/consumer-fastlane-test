package com.handybook.handybook.test.util;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.test.espresso.PerformException;
import android.support.test.espresso.ViewInteraction;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

import java.util.concurrent.TimeoutException;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.scrollTo;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.RootMatchers.withDecorView;
import static android.support.test.espresso.matcher.ViewMatchers.isAssignableFrom;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withParent;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.not;

/**
 * utility class containing non-app-specific methods to check for certain view states
 */
public class ViewUtil
{
    private static final long VIEW_STATE_QUERY_INTERVAL_MS = 50;
    public static final long LONG_MAX_WAIT_TIME_MS = 10000;
    public static final long SHORT_MAX_WAIT_TIME_MS = 5000;

    private ViewUtil()
    {
        //don't want this instantiated. should use static methods only
    }

    public static void waitForViewVisible(int viewId, long maxWaitingTimeMs)
    {
        waitForViewVisibility(withId(viewId), true, maxWaitingTimeMs);
    }

    public static void waitForViewInScrollViewVisible(int viewId, long maxWaitingTimeMs)
    {
        waitForViewInScrollViewVisibility(withId(viewId), true, maxWaitingTimeMs);
    }

    public static void waitForViewNotVisible(int viewId, long maxWaitingTimeMs)
    {
        waitForViewVisibility(withId(viewId), false, maxWaitingTimeMs);
    }

    public static void waitForTextVisible(int stringResourceId, long maxWaitingTimeMs)
    {
        waitForViewVisibility(withText(stringResourceId), true, maxWaitingTimeMs);
    }

    public static void waitForTextNotVisible(int stringResourceId, long maxWaitingTimeMs)
    {
        waitForViewVisibility(withText(stringResourceId), false, maxWaitingTimeMs);
    }

    public static void checkToastDisplayed(int toastStringResourceId, Activity activity)
    {
        onView(withText(toastStringResourceId)).
                inRoot(withDecorView(not(activity.getWindow().getDecorView()))).
                check(matches(isDisplayed()));
    }

    /**
     * waits for the view with the given id to be a given visibility
     * <p/>
     * TODO: cleaner way to do this?
     * TODO: add better error logging
     */
    public static void waitForViewVisibility(
            @NonNull Matcher<View> viewMatcher,
            final boolean visible,
            final long maxWaitingTimeMs
    )
    {
        final long startTime = System.currentTimeMillis();
        final long endTime = startTime + maxWaitingTimeMs;
        while (System.currentTimeMillis() < endTime)
        {
            if (visible == isViewDisplayed(viewMatcher))
            {
                return;
            }

            sleep(VIEW_STATE_QUERY_INTERVAL_MS);
        }
        throw new PerformException.Builder()
                .withActionDescription("wait for view visibility " + visible)
                .withViewDescription("view id: " + viewMatcher.toString())
                .withCause(new TimeoutException())
                .build();
    }

    //TODO consolidate with similar methods
    public static void waitForToastMessageVisibility(
            int toastStringResourceId,
            boolean visible,
            Activity activity,
            final long maxWaitingTimeMs
    )
    {
        ViewInteraction viewInteraction = onView(withText(toastStringResourceId))
                .inRoot(withDecorView(not(activity.getWindow().getDecorView())));
        final long startTime = System.currentTimeMillis();
        final long endTime = startTime + maxWaitingTimeMs;
        while (System.currentTimeMillis() < endTime)
        {
            if (visible == isViewDisplayed(viewInteraction))
            {
                return;
            }

            sleep(VIEW_STATE_QUERY_INTERVAL_MS);
        }
        throw new PerformException.Builder()
                .withActionDescription("wait for toast message visibility ")
                .withViewDescription("view id: " + onView(withText(toastStringResourceId)).toString())
                .withCause(new TimeoutException())
                .build();
    }

    public static void waitForViewInScrollViewVisibility(
            @NonNull Matcher<View> viewMatcher,
            final boolean visible,
            final long maxWaitingTimeMs
    )
    {
        final long startTime = System.currentTimeMillis();
        final long endTime = startTime + maxWaitingTimeMs;
        while (System.currentTimeMillis() < endTime)
        {
            if (visible == isViewInScrollViewDisplayed(viewMatcher))
            {
                return;
            }

            sleep(VIEW_STATE_QUERY_INTERVAL_MS);
        }
        throw new PerformException.Builder()
                .withActionDescription("wait for view visibility " + visible)
                .withViewDescription("view id: " + viewMatcher.toString())
                .withCause(new TimeoutException())
                .build();
    }

    /**
     * checks to see if a view is displayed without throwing an exception if it isn't displayed
     */
    public static boolean isViewDisplayed(int viewId)
    {
        return isViewDisplayed(withId(viewId));
    }

    /**
     * checks to see if a view is displayed without throwing an exception if it isn't displayed
     */
    public static boolean isViewDisplayed(@NonNull Matcher<View> viewMatcher)
    {
        try
        {
            onView(viewMatcher).check(matches(isDisplayed()));
            return true;
        }
        catch (Throwable e)
        {
            return false;
        }
    }

    public static boolean isViewDisplayed(@NonNull ViewInteraction viewInteraction)
    {
        try
        {
            viewInteraction.check(matches(isDisplayed()));
            return true;
        }
        catch (Throwable e)
        {
            return false;
        }
    }

    public static ViewInteraction matchToolbarTitle(int stringResourceId)
    {
        return onView(
                allOf(isAssignableFrom(TextView.class), withParent(isAssignableFrom(Toolbar.class)))
        ).check(matches(withText(stringResourceId)));
    }

    /**
     * checks to see if a view is displayed without throwing an exception if it isn't displayed
     */
    public static boolean isViewInScrollViewDisplayed(@NonNull Matcher<View> viewMatcher)
    {
        try
        {
            onView(viewMatcher).perform(scrollTo()).check(matches(isDisplayed()));
            return true;
        }
        catch (Throwable e)
        {
            return false;
        }
    }

    public static Matcher<View> nthChildOf(final Matcher<View> parentMatcher, final int childPosition)
    {
        return new TypeSafeMatcher<View>()
        {
            @Override
            public void describeTo(Description description)
            {
                description.appendText("position " + childPosition + " of parent ");
                parentMatcher.describeTo(description);
            }

            @Override
            public boolean matchesSafely(View view)
            {
                if (!(view.getParent() instanceof ViewGroup)) { return false; }
                ViewGroup parent = (ViewGroup) view.getParent();

                return parentMatcher.matches(parent)
                        && parent.getChildCount() > childPosition
                        && parent.getChildAt(childPosition).equals(view);
            }
        };
    }

    public static <T> Matcher<T> first(final Matcher<T> matcher)
    {
        return new BaseMatcher<T>()
        {
            boolean isFirst = true;

            @Override
            public boolean matches(final Object item)
            {
                if (isFirst && matcher.matches(item))
                {
                    isFirst = false;
                    return true;
                }

                return false;
            }

            @Override
            public void describeTo(final Description description)
            {
                description.appendText("should return first matching item");
            }
        };
    }

    private static void sleep(final long timeMs)
    {
        try
        {
            Thread.sleep(timeMs);
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }
    }
}
