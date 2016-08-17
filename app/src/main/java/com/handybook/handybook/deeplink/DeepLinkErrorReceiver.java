package com.handybook.handybook.deeplink;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class DeepLinkErrorReceiver extends BroadcastReceiver
{
    @Override
    public void onReceive(final Context context, final Intent intent)
    {
        final boolean success = intent.getBooleanExtra(DeepLinkActivity.EXTRA_SUCCESSFUL, true);
        if (!success)
        {
            final Intent homeIntent = DeepLinkIntentProvider.getHomeIntent(context);
            context.startActivity(homeIntent);
        }
    }
}
