package com.handybook.handybook.manager;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;

import com.crashlytics.android.Crashlytics;
import com.handybook.handybook.constant.PrefsKey;
import com.handybook.handybook.core.ShouldBlockObject;
import com.handybook.handybook.data.DataManager;
import com.handybook.handybook.event.ActivityEvent;
import com.handybook.handybook.ui.activity.BlockingActivity;
import com.squareup.otto.Subscribe;

import javax.inject.Inject;

public class AppBLockManager
{

    private static final long MIN_APP_BLOCKED_CHECK_INTERVAL = 30 * 1000; // no more than every 30s
    private Context mContext;

    @Inject PrefsManager prefsManager;
    @Inject DataManager dataManager;


    @Subscribe
    void onEachActivityResume(final ActivityEvent.Resumed e)
    {
        if(mContext == null){
            mContext = e.getActivity().getApplicationContext();
        }
        if (shoulBlockActivity(e.getActivity()))
        {
            showBlockingScreen();
            e.getActivity().finish();
        }
    }

    private boolean shoulBlockActivity(final Activity activity)
    {
        // If we are resuming an activity other than blocking activity and app is blocked
        return isAppBlocked() && !activity.getClass().equals(BlockingActivity.class);
    }

    private void showBlockingScreen()
    {
        Intent launchBlockingActivity = new Intent(mContext, BlockingActivity.class);
        launchBlockingActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        launchBlockingActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        mContext.startActivity(launchBlockingActivity);
    }

    private boolean isAppBlocked()
    {
        if (shouldUpdateBlockingStateFromApi())
        {
            updateBlockingStateFromApi();
        }
        return prefsManager.getBoolean(PrefsKey.APP_BLOCKED, false);
    }

    private boolean shouldUpdateBlockingStateFromApi()
    {
        final long lastBlockedCheckMillis = prefsManager.getLong(PrefsKey.APP_BLOCKED_LAST_CHECK, 0);
        return System.currentTimeMillis() - lastBlockedCheckMillis > MIN_APP_BLOCKED_CHECK_INTERVAL;
    }

    /**
     * Request ShouldBlock object from API and update shared preferences accordingly
     */
    private void updateBlockingStateFromApi()
    {
        int versionCode;
        final PackageManager packageManager = mContext.getPackageManager();
        final String packageName = mContext.getPackageName();
        try
        {
            versionCode = packageManager.getPackageInfo(packageName, 0).versionCode;
        } catch (PackageManager.NameNotFoundException nnfe)
        {
            nnfe.printStackTrace();
            Crashlytics.logException(nnfe);
            versionCode = 0;
        }
        dataManager.getShouldBlockObject(
                versionCode,
                new DataManager.CacheResponse<ShouldBlockObject>()
                {
                    @Override
                    public void onResponse(final ShouldBlockObject shouldBlockObject)
                    {
                        //Do nothing, what is this even for?
                    }
                },
                new DataManager.Callback<ShouldBlockObject>()
                {
                    @Override
                    public void onSuccess(ShouldBlockObject response)
                    {
                        updateAppBlockedSharedPreference(response.isBlocked());
                    }

                    @Override
                    public void onError(DataManager.DataManagerError error)
                    {
                        Crashlytics.log("Get should block object error.");
                        // Otherwise do nothing. We default to letting people use the app, so in
                        // case of request error we stick to that policy.
                    }
                });

    }

    private void updateAppBlockedSharedPreference(final boolean isBlocked)
    {
        prefsManager.setLong(PrefsKey.APP_BLOCKED_LAST_CHECK, System.currentTimeMillis());
        prefsManager.setBoolean(PrefsKey.APP_BLOCKED, isBlocked);
    }


}
