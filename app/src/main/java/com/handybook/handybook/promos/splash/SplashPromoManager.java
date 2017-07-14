package com.handybook.handybook.promos.splash;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.crashlytics.android.Crashlytics;
import com.handybook.handybook.core.UserManager;
import com.handybook.handybook.core.constant.PrefsKey;
import com.handybook.handybook.core.data.DataManager;
import com.handybook.handybook.core.event.ActivityLifecycleEvent;
import com.handybook.handybook.core.manager.SecurePreferencesManager;
import com.handybook.handybook.core.structures.SerializableHashSet;
import com.handybook.handybook.library.util.DateTimeUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import javax.inject.Inject;

public class SplashPromoManager {
    /*
        manages the display and requests for splash promos and notifications
        since we don't have push messages for these, we must essentially poll
     */

    private final UserManager mUserManager;
    private final DataManager mDataManager;
    private final SecurePreferencesManager mSecurePreferencesManager;
    private final EventBus mBus;

    //every 30 minutes
    private static final long REQUEST_AVAILABLE_PROMO_MIN_DELAY_MS =
            DateTimeUtils.MILLISECONDS_IN_SECOND * DateTimeUtils.SECONDS_IN_MINUTE * 30;
    private long mAvailablePromoLastCheckMs = 0;

    @Inject
    public SplashPromoManager(
            final UserManager userManager,
            final DataManager dataManager,
            final SecurePreferencesManager securePreferencesManager,
            final EventBus bus
    ) {
        mUserManager = userManager;
        mDataManager = dataManager;
        mSecurePreferencesManager = securePreferencesManager;
        mBus = bus;
        mBus.register(this);
    }

    @Subscribe
    public void onEachActivityFragmentsResumed(final ActivityLifecycleEvent.FragmentsResumed e) {
        String userId = null;
        if (mUserManager.getCurrentUser() != null) {
            userId = mUserManager.getCurrentUser().getId();
        }
        //can request for users who are not logged in
        requestAvailableSplashPromo(userId);

    }

    private boolean shouldRequestAvailablePromo() {
        return System.currentTimeMillis() - mAvailablePromoLastCheckMs >
               REQUEST_AVAILABLE_PROMO_MIN_DELAY_MS;
    }

    private void requestAvailableSplashPromo(@Nullable final String userId) {
        if (shouldRequestAvailablePromo()) {
            mAvailablePromoLastCheckMs = System.currentTimeMillis();
            String[] displayedPromos = getDisplayedSplashPromosArray();
            String[] acceptedPromos = getAcceptedSplashPromosArray();
            mDataManager.getAvailableSplashPromo(
                    userId,
                    displayedPromos,
                    acceptedPromos,
                    new DataManager.Callback<SplashPromo>() {
                        @Override
                        public void onSuccess(final SplashPromo splashPromo) {
                            mBus.post(new SplashPromoEvent.ReceiveAvailableSplashPromoSuccess(
                                    splashPromo));
                        }

                        @Override
                        public void onError(final DataManager.DataManagerError error) {
                            mBus.post(new SplashPromoEvent.ReceiveAvailableSplashPromoError(error));
                        }
                    }
            );
        }
    }

    /**
     * remembers this splash promo by storing it in
     * the preferences as specified by the given prefs key
     * @param splashPromo
     * @param prefsKey
     */
    private void rememberSplashPromoInPreferences(
            @NonNull SplashPromo splashPromo,
            @NonNull PrefsKey prefsKey
    ) {
        if (splashPromo.getId() == null) {
            //this should never happen, but just in case
            Crashlytics.logException(new Exception("Splash promo id is null!"));
            return;
        }

        SerializableHashSet splashPromoHashSet = SerializableHashSet.fromJson(
                mSecurePreferencesManager.getString(prefsKey));
        splashPromoHashSet.add(splashPromo.getId());
        mSecurePreferencesManager.setString(prefsKey, splashPromoHashSet.toJson());
    }

    //making these handled as events in case we make this a network call
    @Subscribe
    public void onRequestMarkSplashPromoAsDisplayed(SplashPromoEvent.RequestMarkSplashPromoAsDisplayed event) {
        SplashPromo splashPromo = event.splashPromo;
        rememberSplashPromoInPreferences(splashPromo, PrefsKey.DISPLAYED_SPLASH_PROMOS);
    }

    @Subscribe
    public void onRequestMarkSplashPromoAsAccepted(SplashPromoEvent.RequestMarkSplashPromoAsAccepted event) {
        SplashPromo splashPromo = event.splashPromo;
        rememberSplashPromoInPreferences(splashPromo, PrefsKey.ACCEPTED_SPLASH_PROMOS);
    }

    /**
     * convenience method for converting splash promo preferences object
     * into an array to send to the server
     * @return
     */
    private
    @NonNull
    String[] getDisplayedSplashPromosArray() {
        return SerializableHashSet
                .fromJson(mSecurePreferencesManager.getString(PrefsKey.DISPLAYED_SPLASH_PROMOS))
                .toArray();
    }

    /**
     * convenience method for converting splash promo preferences object
     * into an array to send to the server
     * @return
     */
    private
    @NonNull
    String[] getAcceptedSplashPromosArray() {
        return SerializableHashSet
                .fromJson(mSecurePreferencesManager.getString(PrefsKey.ACCEPTED_SPLASH_PROMOS))
                .toArray();

    }

}
