package com.handybook.handybook.module.notifications.manager;

import android.support.annotation.NonNull;

import com.handybook.handybook.data.DataManager;
import com.handybook.handybook.event.HandyEvent;
import com.handybook.handybook.module.notifications.model.response.HandyNotification;
import com.handybook.handybook.module.notifications.model.response.SplashPromo;
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

//    private static final long REQUEST_AVAILABLE_PROMO_MIN_DELAY_MS =
//            DateTimeUtils.MILLISECONDS_IN_SECOND * DateTimeUtils.SECONDS_IN_MINUTE * 5;

    private static final long REQUEST_AVAILABLE_PROMO_MIN_DELAY_MS = 0;
    //TODO: make this interval longer
    private long availablePromoLastCheckMs = 0;

    //every 5 minutes at most

    private boolean shouldRequestAvailablePromo()
    {
        return System.currentTimeMillis() - availablePromoLastCheckMs > REQUEST_AVAILABLE_PROMO_MIN_DELAY_MS;
    }

    @Subscribe
    public void onRequestAvailablePromo(@NonNull final HandyEvent.RequestAvailableSplashPromo event)
    {
        if(shouldRequestAvailablePromo())
        {
            availablePromoLastCheckMs = System.currentTimeMillis();
            mDataManager.getAvailableSplashPromo(event.userId, new DataManager.Callback<SplashPromo>()
            {
                @Override
                public void onSuccess(final SplashPromo response)
                {
                    mBus.post(new HandyEvent.ReceiveAvailableSplashPromoSuccess(response));
                }

                @Override
                public void onError(final DataManager.DataManagerError error)
                {
                    mBus.post(new HandyEvent.ReceiveAvailableSplashPromoError(error));
                }
            });
        }
        //otherwise do nothing

    }

//    private void requestAvailablePromo(final FragmentActivity fragmentActivity, String userId)
//    {
//        if(shouldRequestAvailablePromo())
//        {
//            availablePromoLastCheckMs = System.currentTimeMillis();
//            mDataManager.getAvailableSplashPromo(userId, new DataManager.Callback<SplashPromo>()
//            {
//                @Override
//                public void onSuccess(final SplashPromo response)
//                {
//                    SplashPromoDialogFragment splashPromoDialogFragment =
//                            SplashPromoDialogFragment.newInstance(response);
//                    splashPromoDialogFragment.show(fragmentActivity.getSupportFragmentManager(), null);
////                    mBus.post(new HandyEvent.ReceiveAvailableSplashPromoSuccess(response));
//                }
//
//                @Override
//                public void onError(final DataManager.DataManagerError error)
//                {
//                    mBus.post(new HandyEvent.ReceiveAvailableSplashPromoError(error));
//                }
//            });
//        }
//    }
//
//    @Subscribe
//    public void onEachActivityResume(final ActivityEvent.Resumed e)
//    {
//        if (e.getActivity() != null)
//        {
//            Context appContext = e.getActivity().getApplicationContext();
//            if(appContext != null && appContext instanceof FragmentActivity)
//            {
//                String userId = user
//                requestAvailablePromo((FragmentActivity) appContext, );
//            }
//        }
//    }
}
