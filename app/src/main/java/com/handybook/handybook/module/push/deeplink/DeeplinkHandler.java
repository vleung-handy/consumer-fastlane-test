package com.handybook.handybook.module.push.deeplink;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.handybook.handybook.booking.ui.activity.BookingDetailActivity;
import com.handybook.handybook.booking.ui.activity.ServiceCategoriesActivity;
import com.handybook.handybook.constant.BundleKeys;

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
            case DeeplinkConstants.DEEPLINK_PRO_RATE_MODAL:
                startServiceCategoriesActivityWithProRate(context, arguments);
                return true;
            default:
                return false;
        }
    }

    private static void startBookingDetailActivity(final Context context, final Bundle arguments)
    {
        Bundle intentArguments = filterArgumentsByKey(arguments, BundleKeys.BOOKING_ID);
        startActivityNewTask(context, BookingDetailActivity.class, intentArguments);
    }

    private static void startServiceCategoriesActivityWithProRate(final Context context,
                                                                  final Bundle arguments)
    {
        Bundle intentArguments = filterArgumentsByKey(arguments, BundleKeys.BOOKING_ID,
                BundleKeys.BOOKING_RATE_PRO_NAME);
        startActivityNewTask(context, ServiceCategoriesActivity.class, intentArguments);
    }

    private static Bundle filterArgumentsByKey(Bundle arguments, String... keys)
    {
        Bundle filteredArguments = new Bundle();
        for (String key : keys)
        {
            filteredArguments.putString(key, arguments.getString(key));
        }
        return filteredArguments;
    }

    private static void startActivityNewTask(final Context context,
                                             final Class<? extends Activity> activityClass,
                                             final Bundle arguments)
    {
        final Intent intent = new Intent(context, activityClass);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtras(arguments);
        context.startActivity(intent);
    }
}
