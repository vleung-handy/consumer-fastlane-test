package com.handybook.handybook.module.notifications.notificationsplash.manager;

import android.support.annotation.NonNull;

import com.handybook.handybook.core.UserManager;
import com.handybook.handybook.data.DataManager;
import com.handybook.handybook.event.ActivityEvent;
import com.handybook.handybook.event.HandyEvent;
import com.handybook.handybook.module.notifications.notificationsplash.model.SplashPromo;
import com.handybook.handybook.util.DateTimeUtils;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import javax.inject.Inject;

public class SplashNotificationManager
{
    /*
        manages the display and requests for splash promos and notifications
        since we don't have push messages for these, we must essentially poll
     */

    private final UserManager mUserManager;
    private final DataManager mDataManager;
    private final Bus mBus;

    //every 30 minutes
    private static final long REQUEST_AVAILABLE_PROMO_MIN_DELAY_MS =
            DateTimeUtils.MILLISECONDS_IN_SECOND * DateTimeUtils.SECONDS_IN_MINUTE * 30;
    private long mAvailablePromoLastCheckMs = 0;

    @Inject
    public SplashNotificationManager(
            final UserManager userManager, final DataManager dataManager,
            final Bus bus
    )
    {
        mUserManager = userManager;
        mDataManager = dataManager;
        mBus = bus;
        mBus.register(this);
    }

    @Subscribe
    public void onEachActivityFragmentsResumed(final ActivityEvent.FragmentsResumed e)
    {
        if(mUserManager.getCurrentUser() != null)
        {
            requestAvailableSplashPromo(mUserManager.getCurrentUser().getId());
        }
    }

    private boolean shouldRequestAvailablePromo()
    {
        return System.currentTimeMillis() - mAvailablePromoLastCheckMs > REQUEST_AVAILABLE_PROMO_MIN_DELAY_MS;
    }

    private void requestAvailableSplashPromo(@NonNull final String userId)
    {
        if(shouldRequestAvailablePromo())
        {
            mAvailablePromoLastCheckMs = System.currentTimeMillis();
            mDataManager.getAvailableSplashPromo(userId, new DataManager.Callback<SplashPromo>()
            {
                @Override
                public void onSuccess(final SplashPromo splashPromo)
                {
                    mBus.post(new HandyEvent.ReceiveAvailableSplashPromoSuccess(splashPromo));
                }

                @Override
                public void onError(final DataManager.DataManagerError error)
                {
                    mBus.post(new HandyEvent.ReceiveAvailableSplashPromoError(error));
                }
            });
        }
    }

}
