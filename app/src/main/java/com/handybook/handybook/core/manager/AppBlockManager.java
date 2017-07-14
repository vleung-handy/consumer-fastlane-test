package com.handybook.handybook.core.manager;

import android.content.Context;
import android.content.pm.PackageManager;

import com.crashlytics.android.Crashlytics;
import com.handybook.handybook.core.BlockedWrapper;
import com.handybook.handybook.core.constant.PrefsKey;
import com.handybook.handybook.core.data.DataManager;
import com.handybook.handybook.core.event.ActivityLifecycleEvent;
import com.handybook.handybook.core.event.HandyEvent;
import com.handybook.handybook.core.ui.activity.BlockingActivity;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

public class AppBlockManager {

    private static final long MIN_BLOCK_CHECK_DELAY_MILLIS = 30 * 1000; // no more than every 30s

    private Context appContext;
    private SecurePreferencesManager mSecurePreferencesManager;
    private DataManager dataManager;
    private EventBus bus;

    public AppBlockManager(
            final EventBus bus,
            final DataManager dataManager,
            final SecurePreferencesManager securePreferencesManager
    ) {
        this.bus = bus;
        this.mSecurePreferencesManager = securePreferencesManager;
        this.dataManager = dataManager;
        this.bus.register(this);
    }

    @Subscribe
    public void onEachActivityFragmentsResumed(final ActivityLifecycleEvent.FragmentsResumed e) {
        if (appContext == null) {
            appContext = e.getActivity().getApplicationContext();
        }
        if (shouldUpdateBlockingStateFromApi()) {
            updateIsBlockedStateFromApi();
        }
        if (isAppBlocked() && !e.getActivity().getClass().equals(BlockingActivity.class)) {
            bus.post(new HandyEvent.StartBlockingAppEvent());
        }
    }

    private boolean isAppBlocked() {
        return mSecurePreferencesManager.getBoolean(PrefsKey.APP_BLOCKED, false);
    }

    private boolean shouldUpdateBlockingStateFromApi() {
        final long lastCheckMillis = mSecurePreferencesManager.getLong(
                PrefsKey.APP_BLOCKED_LAST_CHECK,
                0
        );
        return System.currentTimeMillis() - lastCheckMillis > MIN_BLOCK_CHECK_DELAY_MILLIS;
    }

    /**
     * Request ShouldBlock object from API and update shared preferences accordingly
     */
    private void updateIsBlockedStateFromApi() {
        int versionCode;
        final PackageManager packageManager = appContext.getPackageManager();
        final String packageName = appContext.getPackageName();
        try {
            versionCode = packageManager.getPackageInfo(packageName, 0).versionCode;
        }
        catch (PackageManager.NameNotFoundException nnfe) {
            nnfe.printStackTrace();
            Crashlytics.logException(nnfe);
            versionCode = 0;
        }
        dataManager.getBlockedWrapper(
                versionCode,
                new DataManager.Callback<BlockedWrapper>() {
                    @Override
                    public void onSuccess(BlockedWrapper response) {
                        updateAppBlockedSharedPreference(response.isBlocked());
                    }

                    @Override
                    public void onError(DataManager.DataManagerError error) {
                        final String logMessage = "Error while requesting BlockedWrapper: " + error;
                        Crashlytics.log(logMessage);
                        // Otherwise do nothing. We default to letting people use the app, so in
                        // case of request error we stick to that policy.
                    }
                }
        );

    }

    private void updateAppBlockedSharedPreference(final boolean isBlocked) {
        final boolean wasBlocked = mSecurePreferencesManager.getBoolean(
                PrefsKey.APP_BLOCKED,
                false
        );
        mSecurePreferencesManager.setLong(
                PrefsKey.APP_BLOCKED_LAST_CHECK,
                System.currentTimeMillis()
        );
        mSecurePreferencesManager.setBoolean(PrefsKey.APP_BLOCKED, isBlocked);
        if (!wasBlocked && isBlocked) {// We're starting to block
            bus.post(new HandyEvent.StartBlockingAppEvent());
        }
        else if (wasBlocked && !isBlocked) {// We're stopping blocking
            bus.post(new HandyEvent.StopBlockingAppEvent());

        }
    }


}
