package com.handybook.handybook.deeplink;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import com.google.common.base.Strings;
import com.handybook.handybook.constant.BundleKeys;
import com.handybook.handybook.ui.activity.WebViewActivity;

public class DeepLinkErrorReceiver extends BroadcastReceiver
{
    private static final String FALLBACK_URL = "fallback_url";

    @Override
    public void onReceive(final Context context, final Intent intent)
    {
        final boolean success = intent.getBooleanExtra(DeepLinkActivity.EXTRA_SUCCESSFUL, true);
        if (!success)
        {
            String uriString = intent.getStringExtra(DeepLinkActivity.EXTRA_URI);
            if (!Strings.isNullOrEmpty(uriString))
            {
                Uri uri = Uri.parse(uriString);
                String fallbackUrl = uri.getQueryParameter(FALLBACK_URL);
                if (!Strings.isNullOrEmpty(fallbackUrl))
                {
                    launchWebPage(context, fallbackUrl);
                }
                else
                {
                    launchHome(context);
                }
            }
            else
            {
                launchHome(context);
            }
        }
    }

    private void launchHome(Context context)
    {
        final Intent intent = DeepLinkIntentProvider.getHomeIntent(context);
        context.startActivity(intent);
    }

    private void launchWebPage(Context context, String url)
    {
        final Intent intent = new Intent(context, WebViewActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(BundleKeys.TARGET_URL, url);
        context.startActivity(intent);
    }
}
