package com.handybook.handybook.testutil;

import android.app.Activity;
import android.support.test.espresso.PerformException;

import java.util.concurrent.TimeoutException;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.RootMatchers.withDecorView;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.not;

public class ViewUtils
{
    private final static long MAX_WAITING_TIME_MS = 10000;
    private final static long QUERY_INTERVAL_MS = 50;

    public static void waitForViewVisible(int viewId)
    {
        waitForViewVisibility(viewId, true);
    }

    public static void waitForViewNotVisible(int viewId)
    {
        waitForViewVisibility(viewId, false);
    }

    public static void waitForViewToAppearThenDisappear(int viewId)
    {
        ViewUtils.waitForViewVisible(viewId);
        ViewUtils.waitForViewNotVisible(viewId);
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
     *
     * @param viewId
     * @param visible
     */
    private static void waitForViewVisibility(final int viewId, final boolean visible)
    {
        final long startTime = System.currentTimeMillis();
        final long endTime = startTime + MAX_WAITING_TIME_MS;
        while (System.currentTimeMillis() < endTime)
        {
            if (visible ? isViewDisplayed(viewId) : !isViewDisplayed(viewId))
            {
                return;
            }

            sleep(QUERY_INTERVAL_MS);
        }
        throw new PerformException.Builder()
                .withActionDescription("wait for view visibility " + visible)
                .withViewDescription("view id: " + viewId)
                .withCause(new TimeoutException())
                .build();
    }

    /**
     * checks to see if a view is displayed without throwing an exception if it isn't displayed
     *
     * @param viewId
     * @return
     */
    public static boolean isViewDisplayed(int viewId)
    {
        try
        {
            onView(withId(viewId)).check(matches(isDisplayed()));
            return true;
        }
        catch (Throwable e)
        {
            return false;
        }
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
