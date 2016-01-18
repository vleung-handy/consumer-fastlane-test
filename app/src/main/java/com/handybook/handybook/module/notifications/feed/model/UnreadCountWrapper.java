package com.handybook.handybook.module.notifications.feed.model;

import com.google.gson.annotations.SerializedName;

public class UnreadCountWrapper
{
    @SerializedName("unread_count")
    private int mUnreadCount;

    public int getUnreadCount()
    {
        return mUnreadCount;
    }
}
