package com.handybook.handybook.deeplink;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.crashlytics.android.Crashlytics;
import com.handybook.handybook.util.Utils;

public class DeepLinkUtils
{
    /**
     * handles a deep link without throwing an exception
     *
     * @param deepLink
     * @param context
     * @return true if it was successfully recognized and handled, false otherwise
     */
    public static boolean safeLaunchDeepLink(@Nullable String deepLink, @NonNull Context context)
    {
        if(deepLink == null)
        {
            //Uri.parse() throws an exception if null is the argument
            Crashlytics.logException(new Exception("Received a null deeplink url"));
        }
        else
        {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(deepLink));
            return Utils.safeLaunchIntent(intent, context);
        }
        return false;
    }
}
