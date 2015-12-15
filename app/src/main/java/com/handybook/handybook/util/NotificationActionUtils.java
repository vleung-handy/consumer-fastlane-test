package com.handybook.handybook.util;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import com.handybook.handybook.R;
import com.handybook.handybook.constant.BundleKeys;
import com.handybook.handybook.constant.NotificationActions;
import com.urbanairship.push.notifications.NotificationActionButton;
import com.urbanairship.push.notifications.NotificationActionButtonGroup;

public class NotificationActionUtils
{
    public static NotificationActionButtonGroup getContactActionButtonGroup()
    {
        NotificationActionButton contactCallButton =
                new NotificationActionButton.Builder(NotificationActions.ACTION_CONTACT_CALL)
                .setLabel(R.string.call)
                .setIcon(R.drawable.ic_phone)
                .build();

        NotificationActionButton contactTextButton =
                new NotificationActionButton.Builder(NotificationActions.ACTION_CONTACT_TEXT)
                .setLabel(R.string.text)
                .setIcon(R.drawable.ic_sms)
                .build();

        return new NotificationActionButtonGroup.Builder()
                .addNotificationActionButton(contactCallButton)
                .addNotificationActionButton(contactTextButton)
                .build();
    }

    public static boolean handleAction(final Context context,
                                       final String action,
                                       final Bundle arguments)
    {
        final String bookingPhone = arguments.getString(BundleKeys.BOOKING_PHONE);
        switch (action)
        {
            case NotificationActions.ACTION_CONTACT_CALL:
                return handleContactCallAction(context, bookingPhone);
            case NotificationActions.ACTION_CONTACT_TEXT:
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
