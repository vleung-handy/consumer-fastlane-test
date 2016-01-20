package com.handybook.handybook.module.notifications.splash.manager;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.handybook.handybook.constant.PrefsKey;
import com.handybook.handybook.core.UserManager;
import com.handybook.handybook.data.DataManager;
import com.handybook.handybook.event.ActivityEvent;
import com.handybook.handybook.manager.PrefsManager;
import com.handybook.handybook.module.notifications.splash.SplashNotificationEvent;
import com.handybook.handybook.module.notifications.splash.model.SplashPromo;
import com.handybook.handybook.structures.SerializableHashSet;
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
    private final PrefsManager mPrefsManager;
    private final Bus mBus;

    //every 30 minutes
    private static final long REQUEST_AVAILABLE_PROMO_MIN_DELAY_MS =
            DateTimeUtils.MILLISECONDS_IN_SECOND * DateTimeUtils.SECONDS_IN_MINUTE * 30;
    private long mAvailablePromoLastCheckMs = 0;

    @Inject
    public SplashNotificationManager(
            final UserManager userManager,
            final DataManager dataManager,
            final PrefsManager prefsManager,
            final Bus bus
    )
    {
        mUserManager = userManager;
        mDataManager = dataManager;
        mPrefsManager = prefsManager;
        mBus = bus;
        mBus.register(this);
    }

    @Subscribe
    public void onEachActivityFragmentsResumed(final ActivityEvent.FragmentsResumed e)
    {
        String userId = null;
        if(mUserManager.getCurrentUser() != null)
        {
            userId = mUserManager.getCurrentUser().getId();
        }
        //can request for users who are not logged in
        requestAvailableSplashPromo(userId);

    }

    private boolean shouldRequestAvailablePromo()
    {
        return System.currentTimeMillis() - mAvailablePromoLastCheckMs > REQUEST_AVAILABLE_PROMO_MIN_DELAY_MS;
    }

    private void requestAvailableSplashPromo(@Nullable final String userId)
    {
        if(shouldRequestAvailablePromo())
        {
            mAvailablePromoLastCheckMs = System.currentTimeMillis();
            String[] displayedPromos = getDisplayedSplashPromosArray();
            String[] acceptedPromos = getAcceptedSplashPromosArray();
            mDataManager.getAvailableSplashPromo(userId, displayedPromos, acceptedPromos, new DataManager.Callback<SplashPromo>()
            {
                @Override
                public void onSuccess(final SplashPromo splashPromo)
                {
                    mBus.post(new SplashNotificationEvent.RequestShowSplashPromo(splashPromo));
                }

                @Override
                public void onError(final DataManager.DataManagerError error)
                {
                    mBus.post(new SplashNotificationEvent.ReceiveAvailableSplashPromoError(error));
                }
            });
        }
    }

    /**
     * remembers this splash promo by storing it in
     * the preferences as specified by the given prefs key
     * @param splashPromo
     * @param prefsKey
     */
    private void rememberSplashPromoInPreferences(@NonNull SplashPromo splashPromo, @NonNull PrefsKey prefsKey)
    {
        if(splashPromo.getId() == null)
        {
            //this happens when we launch the promo from the notification feed
            //whose payload doesn't contain the splash promo uniq id
            return;
        }

        SerializableHashSet splashPromoHashSet = SerializableHashSet.fromJson(mPrefsManager.getString(prefsKey));
        splashPromoHashSet.add(splashPromo.getId());
        mPrefsManager.setString(prefsKey, splashPromoHashSet.toJson());
    }

    //making these handled as events in case we make this a network call
    @Subscribe
    public void onRequestMarkSplashPromoAsDisplayed(SplashNotificationEvent.RequestMarkSplashPromoAsDisplayed event)
    {
        SplashPromo splashPromo = event.splashPromo;
        rememberSplashPromoInPreferences(splashPromo, PrefsKey.DISPLAYED_SPLASH_PROMOS);
    }

    @Subscribe
    public void onRequestMarkSplashPromoAsAccepted(SplashNotificationEvent.RequestMarkSplashPromoAsAccepted event)
    {
        SplashPromo splashPromo = event.splashPromo;
        rememberSplashPromoInPreferences(splashPromo, PrefsKey.ACCEPTED_SPLASH_PROMOS);
    }

    /**
     * convenience method for converting splash promo preferences object
     * into an array to send to the server
     * @return
     */
    private @NonNull String[] getDisplayedSplashPromosArray()
    {
        return SerializableHashSet
                .fromJson(mPrefsManager.getString(PrefsKey.DISPLAYED_SPLASH_PROMOS))
                .toArray();
    }

    /**
     * convenience method for converting splash promo preferences object
     * into an array to send to the server
     * @return
     */
    private @NonNull String[] getAcceptedSplashPromosArray()
    {
        return SerializableHashSet
                .fromJson(mPrefsManager.getString(PrefsKey.ACCEPTED_SPLASH_PROMOS))
                .toArray();

    }

}
