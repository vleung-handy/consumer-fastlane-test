package com.handybook.handybook.module.push.action;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import com.handybook.handybook.constant.BundleKeys;
import com.handybook.handybook.util.Utils;

public class PushActionHandler
{
    public static boolean handleAction(final Context context,
                                       final String action,
                                       final Bundle arguments)
    {
        final String bookingPhone = arguments.getString(BundleKeys.BOOKING_PHONE);
        switch (action)
        {
            case PushActionConstants.ACTION_CONTACT_CALL:
                return handleContactCallAction(context, bookingPhone);
            case PushActionConstants.ACTION_CONTACT_TEXT:
                return handleContactTextAction(context, bookingPhone);
        }
        return false;
    }

    private static boolean handleContactCallAction(final Context context, final String bookingPhone)
    {
        if (bookingPhone != null)
        {
            final Intent callIntent = new Intent(Intent.ACTION_VIEW,
                    Uri.fromParts("tel", bookingPhone, null));
            callIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            return Utils.safeLaunchIntent(callIntent, context);
        }
        return false;
    }

    private static boolean handleContactTextAction(final Context context, final String bookingPhone)
    {
        if (bookingPhone != null)
        {
            final Intent textIntent = new Intent(Intent.ACTION_VIEW,
                    Uri.fromParts("sms", bookingPhone, null));
            textIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            return Utils.safeLaunchIntent(textIntent, context);
        }
        return false;
    }
}
