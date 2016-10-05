package com.handybook.handybook.logger.handylogger;


import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

import com.crashlytics.android.Crashlytics;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.handybook.handybook.constant.PrefsKey;
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

import javax.inject.Inject;

public class EventLogManager
{
    private static final String TAG = EventManager.class.getSimpleName();
    private static final String SENT_TIMESTAMP_SECS_KEY = "event_bundle_sent_timestamp";
    private static final int MAX_NUM_PER_BUNDLE = 10;
    private static final Gson GSON = new Gson();

    private static List<EventLogBundle> sEventLogBundles;
    private static EventLogBundle sCurrentEventLogBundle;
    private final Bus mBus;
    private final DataManager mDataManager;
    private final FileManager mFileManager;
    private final SecurePreferencesManager mSecurePreferencesManager;

    private int mSendingLogsCount;
    private boolean mIsSavingLogs;

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
        //Todo, send logs if it exist
        //Todo, load previous bundle if it exist
    }

    @Subscribe
    public synchronized void addLog(@NonNull LogEvent.AddLogEvent event)
    {
        if (true)//!BuildConfig.DEBUG)
        {
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

            //Todo sammy Timerbased, remove the threshold part
            //Only send logs if we're currently not sending any
            if(sCurrentEventLogBundle.size() >= MAX_NUM_PER_BUNDLE && mSendingLogsCount == 0)
                sendLogsFromPreference();

            //todo sammy do we need this?
            //log the payload to Crashlytics too
            try
            {
                //putting in try/catch block just in case GSON.toJson throws an exception
                String eventLogJson = GSON.toJson(event.getLog());
                String crashlyticsLogString = event.getLog().getEventName() + ": " + eventLogJson;
                Crashlytics.log(crashlyticsLogString);
            }
            catch (Exception e)
            {
                Crashlytics.logException(e);
            }

        }
    }

    //TODO facebook auth type is wrong?

    @Subscribe
    public void sendLogsOnStartup(@Nullable final LogEvent.SendLogsEventStartup event)
    {
        //TODO sammy get a list of the file system where the logs are stored to see if there is anything to send

        //This should be done on startup onload retrieve previously stored logs and send immediately.
//        String[] prefBundleString = loadSavedEventLogBundles();
//
//        //This means nothing was stored previously in prefs
//        if (android.text.TextUtils.isEmpty(prefBundleString))
//        {
//            return;
//        }
//
//        sendLogs(prefBundleString);
    }

    public void sendLogsFromPreference()
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
            //Call Upload logs task
            new UploadLogsTask().execute();
        }
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
        saveToPreference(prefsKey, GSON.toJson(eventLogBundles));
    }

    private void saveToPreference(PrefsKey prefsKey, String eventLogBundlesString)
    {
        synchronized (mSecurePreferencesManager)
        {
            mSecurePreferencesManager.setString(prefsKey, eventLogBundlesString);
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

    private class UploadLogsTask extends AsyncTask<Void, Void, Void>
    {
        @Override
        protected Void doInBackground(Void... params)
        {
            String eventLogBundles = loadSavedEventLogBundles(PrefsKey.EVENT_LOG_BUNDLES_TO_SEND);
            //Save this to the file system and remove from original preference
            if(!TextUtils.isEmpty(eventLogBundles))
            {
                saveLogsToFileSystem(eventLogBundles, 0);
            }

            sendLogs();

            return null;
        }

        @Override
        protected void onPostExecute(Void result)
        {
            //Do nothing here.
        }

        private void saveLogsToFileSystem(final String prefBundleString, int retryCount)
        {
            JsonObject[] eventLogBundles = GSON.fromJson(
                    prefBundleString,
                    JsonObject[].class
            );

            for (JsonObject eventLogBundleJson : eventLogBundles)
            {
                Log.e("BLAH", eventLogBundleJson.toString());
                String eventBundleId = eventLogBundleJson.get(EventLogBundle.EVENT_BUNDLE_ID_KEY)
                                                         .getAsString();
                mFileManager.saveLogFile(
                        eventBundleId,
                        eventLogBundleJson.toString()
                );
            }

            //This means they were all saved and we can remove the preference from the system
            // or, if we tried to save the logs 5 times and it fails, then we remove the preference.
            // must means somethings wrong
            if (eventLogBundles.length == mFileManager.getLogFileList().length || retryCount > 5)
            {
                removePreference(PrefsKey.EVENT_LOG_BUNDLES_TO_SEND);

                if(retryCount > 5 && eventLogBundles.length != mFileManager.getLogFileList().length) {
                    Crashlytics.log("Failed to save logs to file system: " + prefBundleString);
                }
            } else {
                //It means not all of it was saved to file system, retry until we hit a limit
                saveLogsToFileSystem(prefBundleString, retryCount++);
            }
        }

        private void sendLogs()
        {
            try
            {
                File[] files = mFileManager.getLogFileList();
                mSendingLogsCount = files.length;
                for (final File file : files)
                {
                    JsonObject eventLogBundle = GSON.fromJson(
                            mFileManager.readFile(file),
                            JsonObject.class
                    );
                    mDataManager.postLogs(
                            eventLogBundle,
                            new DataManager.Callback<EventLogResponse>()
                            {
                                @Override
                                public void onSuccess(EventLogResponse response)
                                {
                                    Log.e("BLAH", "Succesfully uploaded: " + file.getName() + " " +response.getBundleId());
                                    mFileManager.deleteLogFile(response.getBundleId());
                                    mSendingLogsCount--;
                                }

                                @Override
                                public void onError(DataManager.DataManagerError error)
                                {
                                    Log.e("BLAH", "failed: "+error.getMessage() + file.getName());
                                    mSendingLogsCount--;
                                    //todo sammy log this?
                                }
                            }
                    );
                }
            }
            catch (JsonSyntaxException e)
            {
                Crashlytics.logException(e);
                Log.e(TAG, e.getMessage());
                //sreset log count
                mSendingLogsCount = 0;
                //If there's json exception it means logs aren't valid and clear it out
                //TODO sammy remove from file system. means not valid
            }
        }
    }
}
