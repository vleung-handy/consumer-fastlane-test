package com.handybook.handybook.deeplink;

import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;

public class DeepLinkActivity extends com.airbnb.deeplinkdispatch.DeepLinkActivity {

    private DeepLinkFallbackReceiver mDeepLinkFallbackReceiver;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        mDeepLinkFallbackReceiver = new DeepLinkFallbackReceiver();
        LocalBroadcastManager.getInstance(this).registerReceiver(
                mDeepLinkFallbackReceiver,
                new IntentFilter(DeepLinkActivity.ACTION)
        );
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onDestroy() {
        if (mDeepLinkFallbackReceiver != null) {
            LocalBroadcastManager.getInstance(this).unregisterReceiver(mDeepLinkFallbackReceiver);
        }
        super.onDestroy();
    }
}
