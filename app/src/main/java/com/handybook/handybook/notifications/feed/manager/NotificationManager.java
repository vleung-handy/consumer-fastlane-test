package com.handybook.handybook.notifications.feed.manager;

import android.support.annotation.NonNull;

import com.handybook.handybook.core.data.DataManager;
import com.handybook.handybook.core.event.HandyEvent;
import com.handybook.handybook.notifications.feed.model.HandyNotification;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import javax.inject.Inject;

public class NotificationManager
{
    private final DataManager mDataManager;
    private final Bus mBus;

    @Inject
    public NotificationManager(final Bus bus, final DataManager dataManager)
    {
        this.mBus = bus;
        this.mBus.register(this);
        this.mDataManager = dataManager;
    }

    @Subscribe
    public void onRequestNotifications(
            @NonNull final HandyEvent.RequestEvent.HandyNotificationsEvent event
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
                        mBus.post(new HandyEvent.ResponseEvent.HandyNotificationsSuccess(response));
                    }

                    @Override
                    public void onError(DataManager.DataManagerError error)
                    {
                        mBus.post(new HandyEvent.ResponseEvent.HandyNotificationsError(error));
                    }
                });
    }

}
