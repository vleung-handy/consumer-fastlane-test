package com.handybook.handybook.core;


import android.content.Context;
import android.content.Intent;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.google.common.collect.ImmutableMap;
import com.handybook.handybook.booking.ui.activity.BookingsActivity;
import com.handybook.handybook.ui.activity.BaseActivity;

import java.util.LinkedHashMap;
import java.util.Map;

public class HandyWebViewClient extends WebViewClient
{
    private Context mContext;

    private static Map<String, Class<? extends BaseActivity>> ACTIVITY_URL_MAP;

    static
    {
        final Map<String, Class<? extends BaseActivity>> activityUrlMap = new LinkedHashMap<>();
        activityUrlMap.put(".*/users/(?:me|\\d+)", BookingsActivity.class);
        ACTIVITY_URL_MAP = ImmutableMap.copyOf(activityUrlMap);
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
        for (final Map.Entry<String, Class<? extends BaseActivity>> activityUrl :
                ACTIVITY_URL_MAP.entrySet())
        {
            final String urlPattern = activityUrl.getKey();
            final Class<? extends BaseActivity> activityClass = activityUrl.getValue();
            if (url.matches(urlPattern))
            {
                final Intent intent = new Intent(mContext, activityClass);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK
                        | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                mContext.startActivity(intent);
                return true;
            }
        }
        return false;
    }
}
