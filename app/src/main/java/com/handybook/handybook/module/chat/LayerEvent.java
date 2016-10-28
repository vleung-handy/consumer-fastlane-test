package com.handybook.handybook.module.chat;

/**
 * Created by jtse on 10/28/16.
 */
public class LayerEvent
{
    private final long mUnreadMessages;

    public LayerEvent(final long unreadMessages)
    {
        mUnreadMessages = unreadMessages;
    }

    public long getUnreadMessages()
    {
        return mUnreadMessages;
    }
}
