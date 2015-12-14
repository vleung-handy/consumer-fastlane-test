package com.handybook.handybook.module.notifications.manager;

import android.support.annotation.NonNull;

import com.handybook.handybook.data.DataManager;
import com.handybook.handybook.event.HandyEvent;
import com.handybook.handybook.module.notifications.model.response.HandyNotification;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import javax.inject.Inject;

public class NotificationManager
{
    private final DataManager dataManager;
    private final Bus bus;

    @Inject
    public NotificationManager(final Bus bus, final DataManager dataManager)
    {
        this.bus = bus;
        this.dataManager = dataManager;
        this.bus.register(this);
    }

    @Subscribe
    public void onRequestNotifications(
            @NonNull final HandyEvent.RequestEvent.HandyNotificationsEvent event
    )
    {
        dataManager.getNotifications(
                event.getUserId(),
                event.getCount(),
                event.getSinceId(),
                event.getUntilId(),
                new DataManager.Callback<HandyNotification.ResultSet>()
                {
                    @Override
                    public void onSuccess(final HandyNotification.ResultSet response)
                    {
                        bus.post(new HandyEvent.ResponseEvent.HandyNotificationsSuccess(response));
                    }

                    @Override
                    public void onError(DataManager.DataManagerError error)
                    {
                        bus.post(new HandyEvent.ResponseError.HandyNotificationsError(error));
                    }
                });
    }

}
