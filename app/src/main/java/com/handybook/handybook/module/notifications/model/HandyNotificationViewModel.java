package com.handybook.handybook.module.notifications.model;

import android.content.Context;
import android.util.DisplayMetrics;

import com.handybook.handybook.module.notifications.model.response.HandyNotification;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;

public class HandyNotificationViewModel
{
    private HandyNotification mHandyNotification;

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("EEE, d MMM yyyy HH:mm");

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
                return mHandyNotification.getImages()[1].getUrl();
        }
        // TODO: Seriously! Did you redo this?
    }

    public String getTitle()
    {
        return mHandyNotification.getTitle();
    }

    public String getBody()
    {
        return mHandyNotification.getBody();
    }

    public String getTimestamp()
    {
        return DATE_FORMAT.format(mHandyNotification.getCreatedAt().getTime());
    }

    public HandyNotification.HandyNotificationType getType()
    {
        return mHandyNotification.getType();
    }


    public static class List extends ArrayList<HandyNotificationViewModel>
    {
        public static List from(final Collection<HandyNotification> notifications)
        {
            final List notificationViewModelList = new List();
            for (HandyNotification eachNotification : notifications)
            {
                notificationViewModelList.add(new HandyNotificationViewModel(eachNotification));
            }
            return notificationViewModelList;
        }
    }

}
