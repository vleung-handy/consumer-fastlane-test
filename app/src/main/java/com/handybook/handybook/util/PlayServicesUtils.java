package com.handybook.handybook.util;

import android.content.Context;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

/**
 */
public class PlayServicesUtils
{
    public static boolean hasPlayServices(Context context)
    {
        int statusCode = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(context);
        switch (statusCode)
        {
            case ConnectionResult.SUCCESS:
                return true;
            case ConnectionResult.SERVICE_MISSING:
                return false;
            case ConnectionResult.SERVICE_VERSION_UPDATE_REQUIRED:
                return false;
        }
        return false;
    }
}
