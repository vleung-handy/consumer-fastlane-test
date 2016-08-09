package com.handybook.handybook.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.crashlytics.android.Crashlytics;
import com.yozio.android.YozioReferrerReceiver;

public class InstallReferrerReceiver extends BroadcastReceiver
{
    @Override
    public void onReceive(final Context context, final Intent intent)
    {

        triggerYozioReferrerReceiver(context, intent);
        triggerButtonReferrerReceiver(context, intent);
    }

    private void triggerYozioReferrerReceiver(final Context context, final Intent intent)
    {
        /**
         * YozioReferrerReceiver will process the referrer string,
         * fetch out the meta data, and call YozioMetaDataCallback accordingly.
         *
         * Make sure you call YozioReferrerReceiver first. It is an asynchronous method,
         * and will return immediately within a few milliseconds.
         *
         * If you don't put it as the first receiver, the receiver(s) run before it might
         * take too long, and you will run the following risks
         *
         * 1) Yozio receiver never get called before Android kills the process.
         *    In this case, Yozio will fail to track the install or pass the meta data to
         *    your app accordingly.
         *
         * 2) Yozio receiver is called after a long period of time. From several hundred
         *    milliseconds to a few seconds later.
         *    In this case, Yozio will still be able to track the install and pass the meta data
         *    to your app, but you might have missed the opportunity to personalize the 1st time
         *    user experience using the meta data. Like site load speed on the web, every
         *    millisecond counts.
         *
         */
        try
        {
            new YozioReferrerReceiver().onReceive(context, intent);
        }
        catch (Exception e)
        {
            Crashlytics.logException(e);
        }

    }

    private void triggerButtonReferrerReceiver(final Context context, final Intent intent)
    {
        try
        {
            new com.usebutton.sdk.internal.receivers.InstallReferrerReceiver().onReceive(context, intent);
        }
        catch (Exception e)
        {
            Crashlytics.logException(e);
        }

    }
}
