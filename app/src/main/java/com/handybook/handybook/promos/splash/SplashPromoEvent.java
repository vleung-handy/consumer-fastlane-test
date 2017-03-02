package com.handybook.handybook.promos.splash;

import android.support.annotation.NonNull;

import com.handybook.handybook.core.data.DataManager;
import com.handybook.handybook.core.event.HandyEvent;

public abstract class SplashPromoEvent {

    public static class ReceiveAvailableSplashPromoSuccess extends HandyEvent.ReceiveSuccessEvent {

        public final SplashPromo splashPromo;

        public ReceiveAvailableSplashPromoSuccess(final SplashPromo splashPromo) {
            this.splashPromo = splashPromo;
        }
    }


    public static class ReceiveAvailableSplashPromoError extends HandyEvent.ReceiveErrorEvent {

        public ReceiveAvailableSplashPromoError(DataManager.DataManagerError error) {
            this.error = error;
        }
    }


    public static class RequestMarkSplashPromoAsDisplayed extends HandyEvent.RequestEvent {

        public final SplashPromo splashPromo;

        public RequestMarkSplashPromoAsDisplayed(@NonNull final SplashPromo splashPromo) {
            this.splashPromo = splashPromo;
        }
    }


    public static class RequestMarkSplashPromoAsAccepted extends HandyEvent.RequestEvent {

        public final SplashPromo splashPromo;

        public RequestMarkSplashPromoAsAccepted(@NonNull final SplashPromo splashPromo) {
            this.splashPromo = splashPromo;
        }
    }
}
