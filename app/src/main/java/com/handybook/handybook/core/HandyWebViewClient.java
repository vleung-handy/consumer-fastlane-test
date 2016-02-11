package com.handybook.handybook.core;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.google.common.collect.ImmutableList;
import com.handybook.handybook.booking.ui.activity.BookingDetailActivity;
import com.handybook.handybook.booking.ui.activity.BookingsActivity;
import com.handybook.handybook.constant.BundleKeys;
import com.handybook.handybook.ui.activity.BaseActivity;

import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HandyWebViewClient extends WebViewClient
{
    private Context mContext;

    private static List<ActivityUrlMapper> ACTIVITY_URL_MAPPERS;

    static
    {
        final List<ActivityUrlMapper> activityUrlMappers = new LinkedList<>();
        activityUrlMappers.add(new ActivityUrlMapper(
                Pattern.compile(".*/users/(?:me|\\d+)"),
                BookingsActivity.class
        ));
        activityUrlMappers.add(new ActivityUrlMapper(
                Pattern.compile(".*/upcoming/(\\d+)/(?:reschedule_booking|cancel_booking)"),
                BookingDetailActivity.class,
                BundleKeys.BOOKING_ID
        ));
        ACTIVITY_URL_MAPPERS = ImmutableList.copyOf(activityUrlMappers);
    }

    public HandyWebViewClient(final Context context)
    {
        mContext = context;
    }

    @Override
    public boolean shouldOverrideUrlLoading(final WebView view, final String url)
    {
        if (!launchIntentForUrl(url))
        {
            view.loadUrl(url);
        }
        return true;
    }

    private boolean launchIntentForUrl(final String url)
    {
        for (final ActivityUrlMapper activityUrlMapper : ACTIVITY_URL_MAPPERS)
        {
            final Matcher matcher = activityUrlMapper.getUrlPattern().matcher(url);
            if (matcher.matches())
            {
                final Bundle extras = getExtrasFromUrl(activityUrlMapper, matcher);
                final Intent intent = new Intent(mContext, activityUrlMapper.getActivityClass());
                intent.putExtras(extras);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK
                        | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                mContext.startActivity(intent);
                return true;
            }
        }
        return false;
    }

    private Bundle getExtrasFromUrl(
            final ActivityUrlMapper activityUrlMapper,
            final Matcher matcher
    )
    {
        final Bundle arguments = new Bundle();
        final String[] bundleKeys = activityUrlMapper.getBundleKeys();
        for (int i = 0; i < bundleKeys.length; i++)
        {
            final String key = bundleKeys[i];
            arguments.putString(key, matcher.group(i + 1));
        }
        return arguments;
    }

    private static class ActivityUrlMapper
    {
        private final Pattern mUrlPattern;
        private final Class<? extends BaseActivity> mActivityClass;
        private final String[] mBundleKeys;

        ActivityUrlMapper(
                final Pattern urlPattern,
                final Class<? extends BaseActivity> activityClass,
                final String... bundleKeys
        )
        {
            mUrlPattern = urlPattern;
            mActivityClass = activityClass;
            mBundleKeys = bundleKeys;
        }

        public Pattern getUrlPattern()
        {
            return mUrlPattern;
        }

        public Class<? extends BaseActivity> getActivityClass()
        {
            return mActivityClass;
        }

        public String[] getBundleKeys()
        {
            return mBundleKeys;
        }
    }
}
