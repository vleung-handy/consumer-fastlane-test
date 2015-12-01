package com.handybook.handybook.module.notifications.model;

import android.content.Context;
import android.util.DisplayMetrics;

public class HandyNotificationViewModel
{
    private HandyNotification mHandyNotification;

    public HandyNotificationViewModel(final HandyNotification handyNotification)
    {
        mHandyNotification = handyNotification;
    }

    public String getIconUrl(final Context context)
    {
        // TODO: Absolutely redo this before releasing anything!
        switch (context.getResources().getDisplayMetrics().densityDpi)
        {
            case DisplayMetrics.DENSITY_LOW:
                return mHandyNotification.getImages()[0].getUrl();
            case DisplayMetrics.DENSITY_MEDIUM:
                return mHandyNotification.getImages()[1].getUrl();
            case DisplayMetrics.DENSITY_HIGH:
                return mHandyNotification.getImages()[2].getUrl();
            case DisplayMetrics.DENSITY_XHIGH:
                return mHandyNotification.getImages()[3].getUrl();
            case DisplayMetrics.DENSITY_XXHIGH:
                return mHandyNotification.getImages()[4].getUrl();
            case DisplayMetrics.DENSITY_XXXHIGH:
                return mHandyNotification.getImages()[5].getUrl();
            default:
                return mHandyNotification.getImages()[0].getUrl();
        }
    }


}
