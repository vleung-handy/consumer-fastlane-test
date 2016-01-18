package com.handybook.handybook.module.notifications.feed;

import android.support.annotation.NonNull;

import com.handybook.handybook.data.DataManager;
import com.handybook.handybook.event.HandyEvent;
import com.handybook.handybook.module.notifications.feed.model.HandyNotification;
import com.handybook.handybook.module.notifications.feed.model.MarkNotificationsAsReadRequest;

public class NotificationFeedEvent
{
    public static class HandyNotificationsEvent extends HandyEvent.RequestEvent
    {
        final Long mSinceId;
        final Long mUntilId;
        final Long mCount;

        public HandyNotificationsEvent(final Long count, final Long untilId, final Long sinceId)
        {
            mCount = count;
            mUntilId = untilId;
            mSinceId = sinceId;
        }

        public Long getSinceId()
        {
            return mSinceId;
        }

        public Long getUntilId()
        {
            return mUntilId;
        }

        public Long getCount()
        {
            return mCount;
        }
    }


    public static class RequestMarkNotificationAsRead extends HandyEvent.RequestEvent
    {
        public final MarkNotificationsAsReadRequest markNotificationsAsReadRequest;

        public RequestMarkNotificationAsRead(
                @NonNull MarkNotificationsAsReadRequest markNotificationsAsReadRequest
        )
        {
            this.markNotificationsAsReadRequest = markNotificationsAsReadRequest;
        }
    }


    public static class HandyNotificationsSuccess
            extends HandyEvent.ResponseEvent<HandyNotification.ResultSet>
    {
        public HandyNotificationsSuccess(final HandyNotification.ResultSet payload)
        {
            super(payload);
        }
    }


    public static class HandyNotificationsError
            extends HandyEvent.ResponseEvent<DataManager.DataManagerError>
    {
        public HandyNotificationsError(final DataManager.DataManagerError payload)
        {
            super(payload);
        }
    }


    public static class RequestUnreadCount extends HandyEvent.RequestEvent {}


    public static class ReceiveUnreadCountSuccess
            extends HandyEvent.ReceiveSuccessEvent
    {
        private final int mUnreadCount;

        public ReceiveUnreadCountSuccess(final int unreadCount)
        {
            mUnreadCount = unreadCount;
        }

        public int getUnreadCount()
        {
            return mUnreadCount;
        }
    }


    public static class ReceiveUnreadCountError
            extends HandyEvent.ReceiveErrorEvent
    {
        public ReceiveUnreadCountError(final DataManager.DataManagerError error)
        {
            this.error = error;
        }
    }
}
