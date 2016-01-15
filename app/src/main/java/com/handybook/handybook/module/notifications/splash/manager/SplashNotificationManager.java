package com.handybook.handybook.module.notifications.splash.manager;

import android.support.annotation.NonNull;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.handybook.handybook.constant.PrefsKey;
import com.handybook.handybook.core.UserManager;
import com.handybook.handybook.data.DataManager;
import com.handybook.handybook.event.ActivityEvent;
import com.handybook.handybook.manager.PrefsManager;
import com.handybook.handybook.module.notifications.splash.SplashNotificationEvent;
import com.handybook.handybook.module.notifications.splash.model.SplashPromo;
import com.handybook.handybook.util.DateTimeUtils;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import java.lang.reflect.Type;
import java.util.HashSet;

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

    private void requestAvailableSplashPromo(final String userId)
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
                    mBus.post(new SplashNotificationEvent.ReceiveAvailableSplashPromoSuccess(splashPromo));
                }

                @Override
                public void onError(final DataManager.DataManagerError error)
                {
                    mBus.post(new SplashNotificationEvent.ReceiveAvailableSplashPromoError(error));
                }
            });
        }
    }

    //TODO: make sure you refactor everything below this line! super crude due to super rushed feature
    //TODO: REMOVE this class eventually
    /**
     * a set that has convenience methods for storing and retrieving from shared preferences
     */
    public static class PrefsHashSet extends HashSet<String> {

        private static final Type TYPE =
                (new TypeToken<PrefsHashSet>() {}).getType();
        private static final Gson GSON = new Gson();

        public @NonNull String toJsonString()
        {
            return GSON.toJson(this, TYPE);
        }

        public static @NonNull PrefsHashSet fromJsonString(String jsonString)
        {
            PrefsHashSet prefsHashSet = null;
            try
            {
                prefsHashSet = GSON.fromJson(jsonString, TYPE);
            }
            catch (Exception e)
            {
//                e.printStackTrace();
            }
            if(prefsHashSet == null)
            {
                prefsHashSet = new PrefsHashSet();
            }
            return prefsHashSet;
        }
    }

    private @NonNull PrefsHashSet getSplashPromoSet(@NonNull PrefsKey prefsKey)
    {
        return PrefsHashSet.fromJsonString(mPrefsManager.getString(prefsKey));
    }

    //TODO: clean up all of this
    /**
     * add this splash promo to the set retrieved from the prefs key
     * @param splashPromo
     * @param prefsKey
     */
    private void markSplashPromo(@NonNull SplashPromo splashPromo, @NonNull PrefsKey prefsKey)
    //TODO: give better name
    {
        if(splashPromo.getId() == null) return;

        PrefsHashSet prefsHashSet = getSplashPromoSet(prefsKey);
        prefsHashSet.add(splashPromo.getId());
        mPrefsManager.setString(prefsKey, prefsHashSet.toJsonString());
    }

    //making these handled as events in case we make this a network call
    @Subscribe
    public void onRequestMarkSplashPromoAsDisplayed(SplashNotificationEvent.RequestMarkSplashPromoAsDisplayed event)
    {
        SplashPromo splashPromo = event.splashPromo;
        markSplashPromo(splashPromo, PrefsKey.DISPLAYED_SPLASH_PROMOS);
    }

    @Subscribe
    public void onRequestMarkSplashPromoAsAccepted(SplashNotificationEvent.RequestMarkSplashPromoAsAccepted event)
    {
        SplashPromo splashPromo = event.splashPromo;
        markSplashPromo(splashPromo, PrefsKey.ACCEPTED_SPLASH_PROMOS);
    }

    private @NonNull String[] getSplashPromoArray(@NonNull PrefsKey prefsKey)
    {
        PrefsHashSet prefsHashSet = getSplashPromoSet(prefsKey);
        return prefsHashSet.toArray(new String[prefsHashSet.size()]);
    }

    private @NonNull String[] getDisplayedSplashPromosArray()
    {
        return getSplashPromoArray(PrefsKey.DISPLAYED_SPLASH_PROMOS);
    }

    private @NonNull String[] getAcceptedSplashPromosArray()
    {
        return getSplashPromoArray(PrefsKey.ACCEPTED_SPLASH_PROMOS);

    }

}
