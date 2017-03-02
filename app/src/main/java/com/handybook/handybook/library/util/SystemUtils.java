package com.handybook.handybook.library.util;

import android.content.Context;
import android.os.Build;
import android.provider.Settings;

/**
 * utilities to access information about the system
 */
public final class SystemUtils {

    public static String getDeviceId(final Context context) {
        return Settings.Secure.getString(
                context.getContentResolver(),
                Settings.Secure.ANDROID_ID
        );
    }

    public static String getDeviceModel() {
        final String manufacturer = Build.MANUFACTURER;
        final String model = Build.MODEL;

        if (model != null && model.startsWith(manufacturer)) {
            return model;
        }
        else {
            return manufacturer + " " + model;
        }
    }
}
