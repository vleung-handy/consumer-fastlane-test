package com.handybook.handybook.testutil;

import android.support.test.espresso.PerformException;

import java.util.concurrent.TimeoutException;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

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
                .withCause(new TimeoutException())
                .build();
    }

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
