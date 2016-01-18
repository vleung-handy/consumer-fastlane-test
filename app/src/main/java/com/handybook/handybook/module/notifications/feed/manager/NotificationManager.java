package com.handybook.handybook.module.notifications.feed.manager;

import android.support.annotation.NonNull;

import com.handybook.handybook.core.UserManager;
import com.handybook.handybook.data.DataManager;
import com.handybook.handybook.module.notifications.feed.NotificationFeedEvent;
import com.handybook.handybook.module.notifications.feed.model.HandyNotification;
import com.handybook.handybook.module.notifications.feed.model.UnreadCountWrapper;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import javax.inject.Inject;

public class NotificationManager
{
    private final DataManager mDataManager;
    private UserManager mUserManager;
    private final Bus mBus;

    @Inject
    public NotificationManager(
            final Bus bus, final DataManager dataManager, final UserManager userManager
    )
    {
        mBus = bus;
        mDataManager = dataManager;
        mUserManager = userManager;
        mBus.register(this);
    }

    @Subscribe
    public void onRequestNotifications(
            @NonNull final NotificationFeedEvent.HandyNotificationsEvent event
    )
    {
        mDataManager.getNotifications(
                event.getUserId(),
                event.getCount(),
                event.getSinceId(),
                event.getUntilId(),
                new DataManager.Callback<HandyNotification.ResultSet>()
                {
                    @Override
                    public void onSuccess(final HandyNotification.ResultSet response)
                    {
                        mBus.post(new NotificationFeedEvent.HandyNotificationsSuccess(response));
                    }

                    @Override
                    public void onError(DataManager.DataManagerError error)
                    {
                        mBus.post(new NotificationFeedEvent.HandyNotificationsError(error));
                    }
                });
    }

    @Subscribe
    public void onRequestMarkNotificationsAsRead(
            @NonNull final NotificationFeedEvent.RequestMarkNotificationAsRead event
    )
    {
        mDataManager.markNotificationsAsRead(
                event.userId,
                event.markNotificationsAsReadRequest,
                new DataManager.Callback<HandyNotification.ResultSet>()
                {
                    @Override
                    public void onSuccess(final HandyNotification.ResultSet response)
                    {
                        mBus.post(new NotificationFeedEvent.HandyNotificationsSuccess(response));
                    }

                    @Override
                    public void onError(DataManager.DataManagerError error)
                    {
                        mBus.post(new NotificationFeedEvent.HandyNotificationsError(error));
                    }
                });
    }

    @Subscribe
    public void onRequestUnreadCount(
            @NonNull final NotificationFeedEvent.RequestUnreadCount event
    )
    {
        mDataManager.getUnreadNotificationsCount(
                getUserIdIfPresent(),
                new DataManager.Callback<UnreadCountWrapper>()
                {
                    @Override
                    public void onSuccess(final UnreadCountWrapper response)
                    {
                        mBus.post(new NotificationFeedEvent.ReceiveUnreadCountSuccess(
                                response.getUnreadCount()));
                    }

                    @Override
                    public void onError(final DataManager.DataManagerError error)
                    {
                        mBus.post(new NotificationFeedEvent.ReceiveUnreadCountError(error));
                    }
                });
    }

    private long getUserIdIfPresent()
    {
        if (mUserManager.isUserLoggedIn())
        {
            return Long.parseLong(mUserManager.getCurrentUser().getId());
        }
        return 0;
    }
}
