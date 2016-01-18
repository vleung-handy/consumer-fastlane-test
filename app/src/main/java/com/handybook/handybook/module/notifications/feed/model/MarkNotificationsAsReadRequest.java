package com.handybook.handybook.module.notifications.feed.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class MarkNotificationsAsReadRequest
{
    @SerializedName("notification_ids")
    List<Long> mNotificationoIdsToMarkRead;

    public MarkNotificationsAsReadRequest(List<Long> longs)
    {
        mNotificationoIdsToMarkRead = longs;
    }
}
