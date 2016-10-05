package com.handybook.handybook.logger.handylogger;


import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;

import com.crashlytics.android.Crashlytics;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.handybook.handybook.BuildConfig;
import com.handybook.handybook.constant.PrefsKey;
import com.handybook.handybook.core.BaseApplication;
import com.handybook.handybook.core.User;
import com.handybook.handybook.data.DataManager;
import com.handybook.handybook.logger.handylogger.model.Event;
import com.handybook.handybook.logger.handylogger.model.EventLogBundle;
import com.handybook.handybook.logger.handylogger.model.EventLogResponse;
import com.handybook.handybook.manager.FileManager;
import com.handybook.handybook.manager.SecurePreferencesManager;
import com.newrelic.agent.android.analytics.EventManager;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import javax.inject.Inject;

public class EventLogManager
{
    private static final int UPLOAD_TIMER_DELAY = 60000; //1 min
    private static final int UPLOAD_TIMER_DELAY_NO_INTERNET = 15 * 60000; //15 min
    private static final String TAG = EventManager.class.getSimpleName();
    private static final int MAX_NUM_PER_BUNDLE = 50;
    private static final Gson GSON = new Gson();

    private static List<EventLogBundle> sEventLogBundles;
    private static EventLogBundle sCurrentEventLogBundle;
    private final Bus mBus;
    private final DataManager mDataManager;
    private final FileManager mFileManager;
    private final SecurePreferencesManager mSecurePreferencesManager;

    private int mSendingLogsCount;
    private Timer mTimer;

    @Inject
    public EventLogManager(
            final Bus bus, final DataManager dataManager, final FileManager fileManager,
            final SecurePreferencesManager securePreferencesManager
    )
    {
        mBus = bus;
        mBus.register(this);
        mDataManager = dataManager;
        mFileManager = fileManager;
        mSecurePreferencesManager = securePreferencesManager;
        sEventLogBundles = new ArrayList<>();
        //Send logs on initialization
        sendLogsOnInitialization();
    }

    /**
     *
     * @param event
     */
    @Subscribe
    public synchronized void addLog(@NonNull LogEvent.AddLogEvent event)
    {
        if (!BuildConfig.DEBUG)
        { return; }

        //Create upload timer when we get a new log and there isn't a timer currently
        if (mTimer == null)
        { setUploadTimer(); }

        //If event log bundle is null or we've hit the max num per bundle then we create a new bundle
        if (sCurrentEventLogBundle == null || sCurrentEventLogBundle.size() >= MAX_NUM_PER_BUNDLE)
        {
            //Create new event log bundle and add it to the List
            sCurrentEventLogBundle = new EventLogBundle(
                    getUserId(),
                    new ArrayList<Event>()
            );
            sEventLogBundles.add(sCurrentEventLogBundle);
        }

        sCurrentEventLogBundle.addEvent(new Event(event.getLog()));

        //Save the EventLogBundle to preferences always
        saveToPreference(PrefsKey.EVENT_LOG_BUNDLES, sEventLogBundles);
    }

    /**
     * @param prefsKey
     * @return The list of Strings if returned, otherwise, null if nothing was saved in that pref
     * previously
     */
    private String loadSavedEventLogBundles(PrefsKey prefsKey)
    {
        synchronized (mSecurePreferencesManager)
        {
            return mSecurePreferencesManager.getString(prefsKey, null);
        }
    }

    /**
     * Save the List of EventLogBundles to the prefsKey
     *
     * @param prefsKey
     * @param eventLogBundles
     */
    private void saveToPreference(PrefsKey prefsKey, List<EventLogBundle> eventLogBundles)
    {
        synchronized (mSecurePreferencesManager)
        {
            mSecurePreferencesManager.setString(prefsKey, GSON.toJson(eventLogBundles));
        }
    }

    private void removePreference(PrefsKey prefsKey)
    {
        synchronized (mSecurePreferencesManager)
        {
            mSecurePreferencesManager.removeValue(prefsKey);
        }
    }

    private int getUserId()
    {
        User user;
        if ((user = User.fromJson(mSecurePreferencesManager.getString(PrefsKey.USER))) != null)
        {
            try
            {
                return Integer.parseInt(user.getId());
            }
            catch (Exception e)
            {
                return 0;
            }
        }
        return 0;
    }

    //************************************* handle all saving/sending of logs **********************
    private void setUploadTimer()
    {
        if (mTimer != null)
        {
            mTimer.cancel();
            mTimer = null;
        }

        mTimer = new Timer();
        mTimer.schedule(new TimerTask()
        {
            @Override
            public void run()
            {
                sendLogsFromPreference();
            }
            //Check network connection and set timer delay appropriately
        }, hasNetworkConnection() ? UPLOAD_TIMER_DELAY : UPLOAD_TIMER_DELAY_NO_INTERNET);
    }

    private void sendLogsOnInitialization()
    {
        new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                //Check if there was logs that were to be sent but were never saved to file system
                String logBundles = loadSavedEventLogBundles(PrefsKey.EVENT_LOG_BUNDLES_TO_SEND);

                if (!TextUtils.isEmpty(logBundles))
                {
                    //Save the previous ones to file system
                    saveLogsToFileSystem(logBundles, 0);
                }
                //Check regular log bundles from previous run.
                sendLogsFromPreference();
            }
        }).run();
    }

    /**
     * Should be triggered from the timer
     */
    private void sendLogsFromPreference()
    {
        String logBundles = loadSavedEventLogBundles(PrefsKey.EVENT_LOG_BUNDLES);

        if (!TextUtils.isEmpty(logBundles))
        {
            //Save the EventLogBundle to preferences always
            saveToPreference(PrefsKey.EVENT_LOG_BUNDLES_TO_SEND, sEventLogBundles);
            sEventLogBundles.clear();
            sCurrentEventLogBundle = null;
            //delete the old one immediately
            removePreference(PrefsKey.EVENT_LOG_BUNDLES);
        }

        // We need to retrieve the logs previously and save them into the preference as send log key
        // clear out the existing variables for the log manager
        final String prefBundleString = loadSavedEventLogBundles(PrefsKey.EVENT_LOG_BUNDLES_TO_SEND);

        //This means nothing was stored previously in prefs
        if (!android.text.TextUtils.isEmpty(prefBundleString))
        {
            String eventLogBundles = loadSavedEventLogBundles(PrefsKey.EVENT_LOG_BUNDLES_TO_SEND);
            //Save this to the file system and remove from original preference
            if (!TextUtils.isEmpty(eventLogBundles))
            {
                saveLogsToFileSystem(eventLogBundles, 0);
            }
        }

        sendLogs();
    }

    private void saveLogsToFileSystem(final String prefBundleString, int retryCount)
    {
        JsonObject[] eventLogBundles = GSON.fromJson(
                prefBundleString,
                JsonObject[].class
        );

        //Keep a list of the event bundle ids to verify they were all saved
        List<String> eventBundleIds = new ArrayList<>();
        for (JsonObject eventLogBundleJson : eventLogBundles)
        {
            String eventBundleId = eventLogBundleJson.get(EventLogBundle.EVENT_BUNDLE_ID_KEY)
                                                     .getAsString();
            eventBundleIds.add(eventBundleId);
            mFileManager.saveLogFile(
                    eventBundleId,
                    eventLogBundleJson.toString()
            );
        }

        //Make sure all the files were saved
        File[] fileList = mFileManager.getLogFileList();
        for (File file : fileList)
        {
            eventBundleIds.remove(file.getName());
        }

        //This means they were all saved and we can remove the preference from the system. Can't just compare number of files because
        // it may contain files that weren't uploaded previously
        // or, if we tried to save the logs 5 times and it fails, then we remove the preference.
        // must means somethings wrong
        if (eventBundleIds.size() == 0 || retryCount > 5)
        {
            removePreference(PrefsKey.EVENT_LOG_BUNDLES_TO_SEND);

            if (retryCount > 5 && eventBundleIds.size() > 0)
            {
                Crashlytics.log("Failed to save logs to file system: " + prefBundleString);
            }
        }
        else
        {
            //It means not all of it was saved to file system, retry until we hit a limit
            saveLogsToFileSystem(prefBundleString, retryCount++);
        }
    }

    /**
     * handles sending the logs
     */
    private void sendLogs()
    {
        //This is jsut in case there's an invalid json file for some reason
        File invalidFile = null;

        try
        {
            File[] files = mFileManager.getLogFileList();
            mSendingLogsCount = files.length;
            for (final File file : files)
            {
                invalidFile = file;
                //Get each event log bundle
                JsonObject eventLogBundle = GSON.fromJson(
                        mFileManager.readFile(file),
                        JsonObject.class
                );

                //Upload logs
                mDataManager.postLogs(
                        eventLogBundle,
                        new DataManager.Callback<EventLogResponse>()
                        {
                            @Override
                            public void onSuccess(EventLogResponse response)
                            {
                                Log.d(
                                        TAG,
                                        "Succesfully uploaded: " + file.getName() + " " + response.getBundleId()
                                );
                                mFileManager.deleteLogFile(response.getBundleId());
                                finishUpload();
                            }

                            @Override
                            public void onError(DataManager.DataManagerError error)
                            {
                                Log.d(TAG, "failed: " + error.getType() + file.getName());
                                finishUpload();
                            }

                            private void finishUpload()
                            {
                                //If uploads are finished
                                if (--mSendingLogsCount == 0)
                                {
                                    //If there are currently logs, set timer, else clear old timer
                                    if (sEventLogBundles.size() > 0 || mFileManager.getLogFileList().length > 0)
                                    {
                                        setUploadTimer();
                                    }
                                    else
                                    {
                                        mTimer.cancel();
                                        mTimer = null;
                                    }
                                }
                            }
                        }
                );
            }
        }
        catch (JsonSyntaxException e)
        {
            Crashlytics.logException(e);
            Log.e(TAG, e.getMessage());
            //If there's json exception it means logs aren't valid and clear it out
            mFileManager.deleteLogFile(invalidFile.getName());
            //reset log count
            mSendingLogsCount = 0;
        }
    }

    private boolean hasNetworkConnection()
    {
        ConnectivityManager cm =
                (ConnectivityManager) BaseApplication.getContext()
                                                     .getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();

    }
}
