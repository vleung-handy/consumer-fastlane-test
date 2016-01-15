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
        private static final long USER_ID_FOR_LOGGED_OUT_USERS = 0;
        private static final String AUTH_TOKEN_FOR_LOGGED_OUT_USERS = null;

        final long mUserId;
        final String mAuthToken;
        final Long mSinceId;
        final Long  mUntilId;
        final Long  mCount;

        public HandyNotificationsEvent(
                final long userId,
                final String authToken,
                final Long sinceId,
                final Long untilId,
                final Long count
        )
        {
            mUserId = userId;
            mAuthToken = authToken;
            mSinceId = sinceId;
            mUntilId = untilId;
            mCount = count;
        }

        public HandyNotificationsEvent(final Long count, final Long untilId, final Long sinceId)
        {
            mUserId = USER_ID_FOR_LOGGED_OUT_USERS;
            mAuthToken = AUTH_TOKEN_FOR_LOGGED_OUT_USERS;
            mCount = count;
            mUntilId = untilId;
            mSinceId = sinceId;
        }

        public long getUserId()
        {
            return mUserId;
        }

        public String getAuthToken()
        {
            return mAuthToken;
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
        public final long userId;
        public final MarkNotificationsAsReadRequest markNotificationsAsReadRequest;

        public RequestMarkNotificationAsRead(@NonNull final long userId,
                                             @NonNull MarkNotificationsAsReadRequest markNotificationsAsReadRequest)
        {
            this.userId = userId;
            this.markNotificationsAsReadRequest = markNotificationsAsReadRequest;
        }
    }


    public static class HandyNotificationsSuccess extends HandyEvent.ResponseEvent<HandyNotification.ResultSet>
    {
        public HandyNotificationsSuccess(final HandyNotification.ResultSet payload)
        {
            super(payload);
        }
    }


    public static class HandyNotificationsError extends HandyEvent.ResponseEvent<DataManager.DataManagerError>
    {
        public HandyNotificationsError(final DataManager.DataManagerError payload)
        {
            super(payload);
        }
    }
}
