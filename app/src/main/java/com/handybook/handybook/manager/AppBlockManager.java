package com.handybook.handybook.manager;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;

import com.crashlytics.android.Crashlytics;
import com.handybook.handybook.constant.PrefsKey;
import com.handybook.handybook.core.BlockedWrapper;
import com.handybook.handybook.data.DataManager;
import com.handybook.handybook.event.ActivityEvent;
import com.handybook.handybook.event.HandyEvent;
import com.handybook.handybook.ui.activity.BlockingActivity;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

public class AppBlockManager
{
    private static final long MIN_BLOCK_CHECK_DELAY_MILLIS = 30 * 1000; // no more than every 30s

    private Context appContext;
    private PrefsManager prefsManager;
    private DataManager dataManager;
    private Bus bus;

    public AppBlockManager(
            final Bus bus,
            final DataManager dataManager,
            final PrefsManager prefsManager
    )
    {
        this.bus = bus;
        this.prefsManager = prefsManager;
        this.dataManager = dataManager;
        this.bus.register(this);
    }

    @Subscribe
    public void onEachActivityResume(final ActivityEvent.Resumed e)
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
            showBlockingScreen();
            e.getActivity().finish();
        }
    }

    private void showBlockingScreen()
    {
        //TODO: move this out of the manager!
        Intent launchBlockingActivity = new Intent(appContext, BlockingActivity.class);
        launchBlockingActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        launchBlockingActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        appContext.startActivity(launchBlockingActivity);
    }

    private boolean isAppBlocked()
    {
        return prefsManager.getBoolean(PrefsKey.APP_BLOCKED, false);
    }

    private boolean shouldUpdateBlockingStateFromApi()
    {
        final long lastCheckMillis = prefsManager.getLong(PrefsKey.APP_BLOCKED_LAST_CHECK, 0);
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
        final boolean wasBlocked = prefsManager.getBoolean(PrefsKey.APP_BLOCKED, false);
        prefsManager.setLong(PrefsKey.APP_BLOCKED_LAST_CHECK, System.currentTimeMillis());
        prefsManager.setBoolean(PrefsKey.APP_BLOCKED, isBlocked);
        if (!wasBlocked && isBlocked)
        {// We're starting to block
            bus.post(new HandyEvent.StartBlockingAppEvent());
            showBlockingScreen();
        } else if (wasBlocked && !isBlocked)
        {// We're stopping blocking
            bus.post(new HandyEvent.StopBlockingAppEvent());

        }
    }


}
