package com.handybook.handybook.module.notifications.splash;

import android.support.annotation.NonNull;

import com.handybook.handybook.data.DataManager;
import com.handybook.handybook.event.HandyEvent;
import com.handybook.handybook.module.notifications.splash.model.SplashPromo;

public class SplashNotificationEvent
{
    public static class ReceiveAvailableSplashPromoSuccess extends HandyEvent.ReceiveSuccessEvent
    {
        public final SplashPromo splashPromo;
        public ReceiveAvailableSplashPromoSuccess(final SplashPromo splashPromo)
        {
            this.splashPromo = splashPromo;
        }
    }


    public static class ReceiveAvailableSplashPromoError extends HandyEvent.ReceiveErrorEvent
    {
        public ReceiveAvailableSplashPromoError(DataManager.DataManagerError error)
        {
            this.error = error;
        }
    }


    public static class RequestMarkSplashPromoAsDisplayed extends HandyEvent.RequestEvent
    {
        public final SplashPromo splashPromo;
        public RequestMarkSplashPromoAsDisplayed(@NonNull final SplashPromo splashPromo)
        {
            this.splashPromo = splashPromo;
        }
    }


    public static class RequestMarkSplashPromoAsAccepted extends HandyEvent.RequestEvent
    {
        public final SplashPromo splashPromo;
        public RequestMarkSplashPromoAsAccepted(@NonNull final SplashPromo splashPromo)
        {
            this.splashPromo = splashPromo;
        }
    }
}
