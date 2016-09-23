package com.handybook.handybook.manager;

import android.content.Context;
import android.content.pm.PackageManager;

import com.crashlytics.android.Crashlytics;
import com.handybook.handybook.constant.PrefsKey;
import com.handybook.handybook.core.BlockedWrapper;
import com.handybook.handybook.data.DataManager;
import com.handybook.handybook.event.ActivityLifecycleEvent;
import com.handybook.handybook.event.HandyEvent;
import com.handybook.handybook.ui.activity.BlockingActivity;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

public class AppBlockManager
{
    private static final long MIN_BLOCK_CHECK_DELAY_MILLIS = 30 * 1000; // no more than every 30s

    private Context appContext;
    private SecurePreferencesManager mSecurePreferencesManager;
    private DataManager dataManager;
    private Bus bus;

    public AppBlockManager(
            final Bus bus,
            final DataManager dataManager,
            final SecurePreferencesManager securePreferencesManager
    )
    {
        this.bus = bus;
        this.mSecurePreferencesManager = securePreferencesManager;
        this.dataManager = dataManager;
        this.bus.register(this);
    }

    @Subscribe
    public void onEachActivityFragmentsResumed(final ActivityLifecycleEvent.FragmentsResumed e)
    {
        if (appContext == null)
        {
            appContext = e.getActivity().getApplicationContext();
        }
        if (shouldUpdateBlockingStateFromApi())
        {
            updateIsBlockedStateFromApi();
        }
        if (isAppBlocked() && !e.getActivity().getClass().equals(BlockingActivity.class))
        {
            bus.post(new HandyEvent.StartBlockingAppEvent());
        }
    }

    private boolean isAppBlocked()
    {
        return mSecurePreferencesManager.getBoolean(PrefsKey.APP_BLOCKED, false);
    }

    private boolean shouldUpdateBlockingStateFromApi()
    {
        final long lastCheckMillis = mSecurePreferencesManager.getLong(
                PrefsKey.APP_BLOCKED_LAST_CHECK,
                0
        );
        return System.currentTimeMillis() - lastCheckMillis > MIN_BLOCK_CHECK_DELAY_MILLIS;
    }

    /**
     * Request ShouldBlock object from API and update shared preferences accordingly
     */
    private void updateIsBlockedStateFromApi()
    {
        int versionCode;
        final PackageManager packageManager = appContext.getPackageManager();
        final String packageName = appContext.getPackageName();
        try
        {
            versionCode = packageManager.getPackageInfo(packageName, 0).versionCode;
        } catch (PackageManager.NameNotFoundException nnfe)
        {
            nnfe.printStackTrace();
            Crashlytics.logException(nnfe);
            versionCode = 0;
        }
        dataManager.getBlockedWrapper(
                versionCode,
                new DataManager.CacheResponse<BlockedWrapper>()
                {
                    @Override
                    public void onResponse(final BlockedWrapper blockedWrapper)
                    {
                        //Do nothing, what is this even for?
                    }
                },
                new DataManager.Callback<BlockedWrapper>()
                {
                    @Override
                    public void onSuccess(BlockedWrapper response)
                    {
                        updateAppBlockedSharedPreference(response.isBlocked());
                    }

                    @Override
                    public void onError(DataManager.DataManagerError error)
                    {
                        final String logMessage = "Error while requesting BlockedWrapper: " + error;
                        Crashlytics.log(logMessage);
                        // Otherwise do nothing. We default to letting people use the app, so in
                        // case of request error we stick to that policy.
                    }
                });

    }

    private void updateAppBlockedSharedPreference(final boolean isBlocked)
    {
        final boolean wasBlocked = mSecurePreferencesManager.getBoolean(
                PrefsKey.APP_BLOCKED,
                false
        );
        mSecurePreferencesManager.setLong(
                PrefsKey.APP_BLOCKED_LAST_CHECK,
                System.currentTimeMillis()
        );
        mSecurePreferencesManager.setBoolean(PrefsKey.APP_BLOCKED, isBlocked);
        if (!wasBlocked && isBlocked)
        {// We're starting to block
            bus.post(new HandyEvent.StartBlockingAppEvent());
        } else if (wasBlocked && !isBlocked)
        {// We're stopping blocking
            bus.post(new HandyEvent.StopBlockingAppEvent());

        }
    }


}
