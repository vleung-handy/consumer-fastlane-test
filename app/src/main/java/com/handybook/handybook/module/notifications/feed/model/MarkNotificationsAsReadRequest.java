package com.handybook.handybook.module.notifications.feed.model;

import com.google.gson.annotations.SerializedName;

public class MarkNotificationsAsReadRequest
{
    @SerializedName("notification_ids")
    long mNotificationIdArray[];

    public MarkNotificationsAsReadRequest(long notificationIdArray[])
    {
        mNotificationIdArray = notificationIdArray;
    }
}
