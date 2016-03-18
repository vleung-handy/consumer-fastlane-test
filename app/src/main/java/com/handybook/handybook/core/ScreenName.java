package com.handybook.handybook.core;

import android.app.Activity;


/**
 * ScreenName will eventually map Activities and Fragments to their String names for
 * Analytucs purposes, unless we will find a better way to go about this.
 */
public class ScreenName
{
    public static final String from(final Activity activity)
    {
        return activity.getClass().getSimpleName();
    }
}
