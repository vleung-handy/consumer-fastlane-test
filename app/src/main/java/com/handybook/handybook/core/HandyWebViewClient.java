package com.handybook.handybook.core;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class HandyWebViewClient extends WebViewClient
{
    private Context mContext;

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
        for (final ActivityUrlMapper activityUrlMapper : ActivityUrlMapper.values())
        {
            if (activityUrlMapper.matches(url))
            {
                final Bundle extras = activityUrlMapper.getExtrasFromUrl(url);
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
}
