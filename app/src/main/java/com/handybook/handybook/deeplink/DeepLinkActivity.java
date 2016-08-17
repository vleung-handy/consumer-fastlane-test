package com.handybook.handybook.deeplink;

import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;

public class DeepLinkActivity extends com.airbnb.deeplinkdispatch.DeepLinkActivity
{
    private DeepLinkErrorReceiver mDeepLinkErrorReceiver;

    @Override
    protected void onCreate(final Bundle savedInstanceState)
    {
        mDeepLinkErrorReceiver = new DeepLinkErrorReceiver();
        LocalBroadcastManager.getInstance(this).registerReceiver(mDeepLinkErrorReceiver,
                new IntentFilter(DeepLinkActivity.ACTION));
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onDestroy()
    {
        if (mDeepLinkErrorReceiver != null)
        {
            LocalBroadcastManager.getInstance(this).unregisterReceiver(mDeepLinkErrorReceiver);
        }
        super.onDestroy();
    }
}
