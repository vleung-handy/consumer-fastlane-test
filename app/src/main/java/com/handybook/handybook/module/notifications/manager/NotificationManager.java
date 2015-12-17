package com.handybook.handybook.module.notifications.manager;

import android.support.annotation.NonNull;

import com.handybook.handybook.constant.PrefsKey;
import com.handybook.handybook.data.DataManager;
import com.handybook.handybook.event.HandyEvent;
import com.handybook.handybook.manager.PrefsManager;
import com.handybook.handybook.model.response.SplashPromo;
import com.handybook.handybook.module.notifications.model.response.HandyNotification;
import com.handybook.handybook.util.DateTimeUtils;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import javax.inject.Inject;

public class NotificationManager
{
    private final DataManager mDataManager;
    private final PrefsManager mPrefsManager;

    private final Bus mBus;

    @Inject
    public NotificationManager(final Bus bus, final DataManager dataManager, final PrefsManager prefsManager)
    {
        this.mBus = bus;
        this.mBus.register(this);
        this.mDataManager = dataManager;
        this.mPrefsManager = prefsManager;
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

    private static final long REQUEST_AVAILABLE_PROMO_MIN_DELAY_MS =
            DateTimeUtils.MILLISECONDS_IN_SECOND * DateTimeUtils.SECONDS_IN_MINUTE * 5;
    //every 5 minutes at most

    private boolean shouldRequestAvailablePromo()
    {
        final long lastCheckMillis = mPrefsManager.getLong(PrefsKey.AVAILABLE_PROMO_LAST_CHECK_MS, 0);
        return System.currentTimeMillis() - lastCheckMillis > REQUEST_AVAILABLE_PROMO_MIN_DELAY_MS;
    }

    @Subscribe
    public void onRequestAvailablePromo(@NonNull final HandyEvent.RequestEvent.RequestAvailableSplashPromo event)
    {
        if(shouldRequestAvailablePromo())
        {
            mPrefsManager.setLong(PrefsKey.AVAILABLE_PROMO_LAST_CHECK_MS, System.currentTimeMillis());
            mDataManager.getAvailableSplashPromo(event.userId, new DataManager.Callback<SplashPromo>()
            {
                @Override
                public void onSuccess(final SplashPromo response)
                {
                    mBus.post(new HandyEvent.ResponseEvent.ReceiveAvailableSplashPromoSuccess(response));
                }

                @Override
                public void onError(final DataManager.DataManagerError error)
                {
                    mBus.post(new HandyEvent.ResponseEvent.ReceiveAvailableSplashPromoError(error));
                }
            });
        }
        //otherwise do nothing

    }
}
