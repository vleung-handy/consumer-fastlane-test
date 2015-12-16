package com.handybook.handybook.module.push.deeplink;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.handybook.handybook.constant.BundleKeys;
import com.handybook.handybook.ui.activity.BookingDetailActivity;

public class DeeplinkHandler
{
    public static boolean handleDeeplink(final Context context, final Bundle arguments)
    {
        final String deeplink = arguments.getString(BundleKeys.DEEPLINK);
        if (deeplink == null)
        {
            return false;
        }
        switch (deeplink)
        {
            case DeeplinkConstants.DEEPLINK_DETAIL_BOOKING_MODAL:
                startBookingDetailActivity(context, arguments);
                return true;
            default:
                return false;
        }
    }

    private static void startBookingDetailActivity(final Context context, final Bundle arguments)
    {
        final Intent intent = new Intent(context, BookingDetailActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(BundleKeys.BOOKING_ID, arguments.getString(BundleKeys.BOOKING_ID));
        context.startActivity(intent);
    }
}
