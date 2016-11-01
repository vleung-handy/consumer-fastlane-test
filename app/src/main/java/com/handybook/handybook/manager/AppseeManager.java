package com.handybook.handybook.manager;

import android.support.annotation.NonNull;
import android.view.View;

import com.appsee.Appsee;
import com.crashlytics.android.Crashlytics;
import com.handybook.handybook.module.configuration.manager.ConfigurationManager;
import com.handybook.handybook.module.configuration.model.Configuration;

/**
 * wrapping Appsee in a manager in case we want more control over it ex. easily toggle it on for
 * specific users only via configs, or catch exceptions
 */
public class AppseeManager
{
    private final String mAppSeeApiKey;
    private final ConfigurationManager mConfigurationManager;
    private final FileManager mFileManager;

    /**
     * we won't enable appsee recording if device doesn't have at least this much storage space
     */
    private static final long LOW_STORAGE_SPACE_THRESHOLD_MEGABYTES = 500; //500mb

    public AppseeManager(
            @NonNull final String appseeApiKey,
            @NonNull final ConfigurationManager configurationManager,
            @NonNull final FileManager fileManager
    )
    {
        mAppSeeApiKey = appseeApiKey;
        mConfigurationManager = configurationManager;
        mFileManager = fileManager;
    }

    /**
     * Appsee appears to use default internal storage directory for video storage
     *
     * this method should not cause a crash as all exceptions are caught
     * @return
     */
    private boolean isEnoughSpaceAvailableForRecording()
    {
        long freeSpaceBytes = mFileManager.getInternalStorageDirectoryFreeSpaceBytes();
        if (freeSpaceBytes < 0)
        {
            addAppseeEvent("unable to get files directory free space");
        }
        long freeSpaceMegabytes = freeSpaceBytes / 1000000;
        return freeSpaceMegabytes >= LOW_STORAGE_SPACE_THRESHOLD_MEGABYTES;
    }

    /**
     * @return whether Appsee.start() can be called
     */
    private boolean isAppseeEnabled()
    {
        boolean isEnoughStorageSpaceAvailableForRecording = isEnoughSpaceAvailableForRecording();
        if (!isEnoughStorageSpaceAvailableForRecording)
        {
            String logErrorMessage = "not enabling Appsee - low disk space (<" + LOW_STORAGE_SPACE_THRESHOLD_MEGABYTES + "mb)";
            Crashlytics.logException(new Exception(logErrorMessage)); //simple way for us to be aware of how often this happens in practice
            addAppseeEvent(logErrorMessage); //log in Appsee so we don't get confused when no recording
        }
        Configuration configuration = mConfigurationManager.getPersistentConfiguration();
        return configuration != null
                && configuration.isAppseeAnalyticsEnabled()
                && isEnoughStorageSpaceAvailableForRecording;
    }

    private void addAppseeEvent(String eventText)
    {
        try
        {
            Appsee.addEvent(eventText);
        }
        catch (Exception e)
        {
            Crashlytics.logException(e);
        }
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

        startRecording();
    }

    /**
     * starts recording the screen does nothing if recording was already started
     */
    private void startRecording()
    {
        try
        {
            Appsee.start(mAppSeeApiKey);
        }
        catch (Exception e)
        {
            Crashlytics.logException(e);
        }
    }

    /**
     * stops recording the screen
     * does nothing if recording was not started
     */
    private void stopRecording()
    {
        try
        {
            Appsee.stop();
        }
        catch (Exception e)
        {
            Crashlytics.logException(e);
        }
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
    public static void markViewsAsSensitive(View... views)
    {
        for (View view : views)
        {
            try
            {
                Appsee.markViewAsSensitive(view);
            }
            catch (Exception e)
            {
                Crashlytics.logException(e);
            }
        }
    }
}
