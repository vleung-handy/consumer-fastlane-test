package com.handybook.handybook.core;

import android.os.Bundle;

import com.handybook.handybook.booking.ui.activity.BookingDetailActivity;
import com.handybook.handybook.booking.ui.activity.BookingsActivity;
import com.handybook.handybook.booking.ui.activity.ServiceCategoriesActivity;
import com.handybook.handybook.constant.BundleKeys;
import com.handybook.handybook.ui.activity.BaseActivity;
import com.handybook.handybook.ui.activity.ProfileActivity;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public enum ActivityUrlMapper
{
    NEW_QUOTE(ServiceCategoriesActivity.class, Pattern.compile(".*/quotes/new.*")),
    ACCOUNT(ProfileActivity.class, Pattern.compile(".*/(?:users|accounts)/(?:me|\\d+)/edit")),
    BOOKINGS(BookingsActivity.class, Pattern.compile(".*/(?:users|accounts)/(?:me|\\d+)")),
    BOOKING_DETAIL(
            BookingDetailActivity.class,
            Pattern.compile(".*/upcoming/(\\d+)/(?:reschedule_booking|cancel_booking)"),
            BundleKeys.BOOKING_ID
    ),;

    private final Class<? extends BaseActivity> mActivityClass;
    private final Pattern mUrlPattern;
    private final String[] mExtrasKeys;

    ActivityUrlMapper(
            final Class<? extends BaseActivity> activityClass,
            final Pattern urlPattern,
            final String... extrasKeys
    )
    {
        mActivityClass = activityClass;
        mUrlPattern = urlPattern;
        mExtrasKeys = extrasKeys;
    }

    public Class<? extends BaseActivity> getActivityClass()
    {
        return mActivityClass;
    }

    public boolean matches(final String url)
    {
        return getMatcher(url).matches();
    }

    public Bundle getExtrasFromUrl(final String url)
    {
        final Matcher matcher = getMatcher(url);
        final Bundle arguments = new Bundle();
        for (int i = 0; i < mExtrasKeys.length; i++)
        {
            final String key = mExtrasKeys[i];
            arguments.putString(key, matcher.group(i + 1));
        }
        return arguments;
    }

    private Matcher getMatcher(final String url)
    {
        return mUrlPattern.matcher(url);
    }
}
