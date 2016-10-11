package com.handybook.handybook.manager;

import android.support.annotation.NonNull;
import android.view.View;

import com.appsee.Appsee;
import com.handybook.handybook.module.configuration.manager.ConfigurationManager;
import com.handybook.handybook.module.configuration.model.Configuration;

/**
 * wrapping Appsee in a manager in case we want more control over it ex. easily toggle it on for
 * specific users only via configs, or catch exceptions
 */
public class AppseeManager
{
    private final String mAppSeeApiKey;
    private ConfigurationManager mConfigurationManager;

    public AppseeManager(
            @NonNull final String appseeApiKey,
            @NonNull final ConfigurationManager configurationManager
    )
    {
        mAppSeeApiKey = appseeApiKey;
        mConfigurationManager = configurationManager;
    }

    /**
     * @return whether Appsee.start() can be called
     */
    public boolean isAppseeEnabled()
    {
        Configuration configuration = mConfigurationManager.getPersistentConfiguration();
        return configuration != null && configuration.isAppseeAnalyticsEnabled();
    }

    /**
     * starts recording the screen if Appsee is enabled
     * (based on resulting videos, starting recording again after already started will have no effect)
     * else stops recording the screen
     *
     * according to docs, should ONLY be called from Activity.onCreate() or Activity.onResume()
     */
    public void startOrStopRecordingAsNecessary()
    {
        if (!isAppseeEnabled())
        {
            stopRecording();
            //in case configuration changed from recording enabled -> disabled
            return;
        }

        Appsee.start(mAppSeeApiKey);
    }

    /**
     * stops recording the screen
     * does nothing if recording was not started
     */
    public void stopRecording()
    {
        Appsee.stop();
    }

    /**
     * marks the given views as sensitive so that they are blocked in the Appsee recordings NOTE:
     * currently unused because it looks like all edit text views are blocked out of the recording
     * by default
     * <p>
     * does nothing if recording was not started
     *
     * @param views
     */
    public void markViewsAsSensitive(View... views)
    {
        for (View view : views)
        {
            Appsee.markViewAsSensitive(view);
        }
    }
}
