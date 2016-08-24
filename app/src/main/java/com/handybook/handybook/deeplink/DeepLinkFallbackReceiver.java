package com.handybook.handybook.deeplink;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import com.google.common.base.Strings;
import com.handybook.handybook.constant.BundleKeys;
import com.handybook.handybook.ui.activity.WebViewActivity;

public class DeepLinkFallbackReceiver extends BroadcastReceiver
{
    private static final String FALLBACK_URL = "fallback_url";
    public static final String KEY_YOZIO_METADATA = "__ymd";
    public static final String KEY_YOZIO_TARGET_ACTIVITY = "__yta";

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
                final Intent yozioIntent = getYozioIntent(context, uri);
                if (yozioIntent != null)
                {
                    context.startActivity(yozioIntent);
                }
                else
                {
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
            }
            else
            {
                launchHome(context);
            }
        }
    }

    private Intent getYozioIntent(final Context context, final Uri uri)
    {
        final String metadata = uri.getQueryParameter(KEY_YOZIO_METADATA);
        if (Strings.isNullOrEmpty(metadata))
        {
            return null;
        }
        final String targetActivity = new Uri.Builder().encodedQuery(metadata).build()
                .getQueryParameter(KEY_YOZIO_TARGET_ACTIVITY);
        if (Strings.isNullOrEmpty(targetActivity))
        {
            return null;
        }
        final Intent intent = new Intent();
        intent.setComponent(new ComponentName(context.getPackageName(), targetActivity));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK
                | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        return intent;
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
