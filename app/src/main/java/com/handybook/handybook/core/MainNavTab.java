package com.handybook.handybook.core;

import android.support.annotation.NonNull;

/**
 * root level tabs for the main nav flow
 * todo is the name confusing?
 */
public enum MainNavTab
{
    BOOKINGS,
    PRO_TEAM,
    SERVICES,
    SHARE,
    ACCOUNT,
    UNKNOWN;

    /**
     *
     * responsible for main nav tab navigation. currently implemented by bottom nav activity.
     * this is an interface just in case we want to switch to a different navigator
     */
    public interface Navigator
    {
        boolean navigateToMainNavTab(@NonNull MainNavTab mainNavTab);
    }
}
