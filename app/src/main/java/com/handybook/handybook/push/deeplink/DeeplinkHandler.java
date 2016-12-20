package com.handybook.handybook.push.deeplink;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;

import com.handybook.handybook.booking.ui.activity.BookingDetailActivity;
import com.handybook.handybook.core.constant.BundleKeys;
import com.handybook.handybook.core.ui.activity.SplashActivity;

public class DeeplinkHandler
{
    public static boolean handleDeeplink(@NonNull final Context context,
                                         @NonNull final Bundle arguments)
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
        final Intent intent = getLaunchIntent(context, BookingDetailActivity.class,
                intentArguments);
        context.startActivity(intent);
    }

    private static void startServiceCategoriesActivityWithProRate(final Context context,
                                                                  final Bundle arguments)
    {
        Bundle intentArguments = filterArgumentsByKey(arguments, BundleKeys.BOOKING_ID,
                BundleKeys.BOOKING_RATE_PRO_NAME);
        final Intent intent = getLaunchIntent(context, SplashActivity.class,
                intentArguments);
        intent.setAction(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        context.startActivity(intent);
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

    private static Intent getLaunchIntent(final Context context,
                                          final Class<? extends Activity> activityClass,
                                          final Bundle arguments)
    {
        final Intent intent = new Intent(context, activityClass);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK |
                Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtras(arguments);
        return intent;
    }
}
