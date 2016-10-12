package com.handybook.handybook.logger.handylogger;


import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;
import android.text.TextUtils;
import android.util.Log;

import com.crashlytics.android.Crashlytics;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSyntaxException;
import com.handybook.handybook.BuildConfig;
import com.handybook.handybook.constant.PrefsKey;
import com.handybook.handybook.core.BaseApplication;
import com.handybook.handybook.core.User;
import com.handybook.handybook.core.UserManager;
import com.handybook.handybook.data.DataManager;
import com.handybook.handybook.library.util.PropertiesReader;
import com.handybook.handybook.logger.handylogger.model.Event;
import com.handybook.handybook.logger.handylogger.model.EventLogBundle;
import com.handybook.handybook.logger.handylogger.model.EventLogResponse;
import com.handybook.handybook.logger.handylogger.model.Session;
import com.handybook.handybook.manager.DefaultPreferencesManager;
import com.handybook.handybook.manager.FileManager;
import com.mixpanel.android.mpmetrics.MixpanelAPI;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import javax.inject.Inject;

public class EventLogManager
{
    private static final int UPLOAD_TIMER_DELAY_MS = 60000; //1 min
    private static final int DEFAULT_USER_ID = -1;
    private static final int MAX_RETRY_COUNT = 5;
    private static final String SENT_TIMESTAMP_SECS_KEY = "event_bundle_sent_timestamp";
    private static final int UPLOAD_TIMER_DELAY = 60000; //1 min
    private static final int UPLOAD_TIMER_DELAY_NO_INTERNET_MS = 15 * UPLOAD_TIMER_DELAY; //15 min
    private static final String TAG = EventLogManager.class.getSimpleName();
    static final int MAX_NUM_PER_BUNDLE = 50;
    private static final Gson GSON = new Gson();

    private static List<EventLogBundle> sEventLogBundles;
    private static EventLogBundle sCurrentEventLogBundle;
    private final Bus mBus;
    private final DataManager mDataManager;
    private final FileManager mFileManager;
    private final DefaultPreferencesManager mPrefsManager;
    private final MixpanelAPI mMixpanel;
    private Session mSession;
    //Used just for mixed panel
    private UserManager mUserManager;

    private int mSendingLogsCount;
    private Timer mTimer;

    @Inject
    public EventLogManager(
            final Bus bus,
            final DataManager dataManager,
            final FileManager fileManager,
            final DefaultPreferencesManager prefsManager,
            final UserManager userManager
    )
    {
        mBus = bus;
        mBus.register(this);
        mDataManager = dataManager;
        mFileManager = fileManager;
        mPrefsManager = prefsManager;
        mUserManager = userManager;
        sEventLogBundles = new ArrayList<>();
        //Send logs on initialization
        sendLogsOnInitialization();

        String mixPanelProperty = BuildConfig.FLAVOR.equals(BaseApplication.FLAVOR_PROD) ? "mixpanel_api_key" : "mixpanel_api_key_internal";
        String mixpanelApiKey = PropertiesReader.getProperties(BaseApplication.getContext(), "config.properties").getProperty(mixPanelProperty);
        mMixpanel = MixpanelAPI.getInstance(BaseApplication.getContext(), mixpanelApiKey);

        //Session
        mSession = Session.getInstance(mPrefsManager);
    }

    /**
     * @param event
     */
    @Subscribe
    public synchronized void addLog(@NonNull LogEvent.AddLogEvent event)
    {
        mSession.incrementEventCount(mPrefsManager);
        Event eventLog = new Event(event.getLog(), mSession.getId(), mSession.getEventCount());

        //log the payload to Crashlytics too
        //Note: Should always log regardless of flavor/variant

        //Create upload timer when we get a new log and there isn't a timer currently
        if (mTimer == null) { setUploadTimer(); }

        //log the payload to Crashlytics too, useful for follow steps for debugging when crash
        try
        {
            //putting in try/catch block just in case GSON.toJson throws an exception
            //Get the log only to log
            JSONObject eventLogJson = new JSONObject(GSON.toJson(event.getLog()));
            String logString = event.getLog().getEventName() + ": " + eventLogJson.toString();
            Crashlytics.log(logString);

            //Mixpanel tracking info in NOR-1016
            addMixPanelProperties(eventLogJson, eventLog);
            mMixpanel.track(eventLog.getEventType(), eventLogJson);
        }
        catch (JsonParseException e)
        {
            Crashlytics.logException(e);
        }
        catch (JSONException e)
        {
            Crashlytics.logException(e);
        }

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

        //Prefix event_type with app_lib_
        eventLog.setEventType("app_lib_" + eventLog.getEventType());
        sCurrentEventLogBundle.addEvent(eventLog);

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
        synchronized (mPrefsManager)
        {
            return mPrefsManager.getString(prefsKey, null);
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
        try
        {
            mPrefsManager.setString(prefsKey, GSON.toJson(eventLogBundles));
        }
        catch (JsonParseException e)
        {
            //If there's an JsonParseException then clear the eventLogBundles because invalid json
            sEventLogBundles.clear();
        }
    }

    private void removePreference(PrefsKey prefsKey)
    {
        synchronized (mPrefsManager)
        {
            mPrefsManager.removeValue(prefsKey);
        }
    }

    private int getUserId()
    {
        User user;
        if ((user = User.fromJson(mPrefsManager.getString(PrefsKey.USER))) != null)
        {
            try
            {
                return Integer.parseInt(user.getId());
            }
            catch (Exception e)
            {
                return DEFAULT_USER_ID;
            }
        }
        return DEFAULT_USER_ID;
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
        }, hasNetworkConnection() ? UPLOAD_TIMER_DELAY_MS : UPLOAD_TIMER_DELAY_NO_INTERNET_MS);
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
    @VisibleForTesting
    void sendLogsFromPreference()
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
        if (!TextUtils.isEmpty(prefBundleString))
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
            String eventBundleId = eventLogBundleJson.get(EventLogBundle.KEY_EVENT_BUNDLE_ID)
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
        if (eventBundleIds.isEmpty() || retryCount > MAX_RETRY_COUNT)
        {
            removePreference(PrefsKey.EVENT_LOG_BUNDLES_TO_SEND);

            if (retryCount > MAX_RETRY_COUNT && !eventBundleIds.isEmpty())
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

                //Add the sent timestamp value
                eventLogBundle.addProperty(
                        SENT_TIMESTAMP_SECS_KEY,
                        System.currentTimeMillis() / 1000
                );

                //Upload logs
                mDataManager.postLogs(
                        eventLogBundle,
                        new DataManager.Callback<EventLogResponse>()
                        {
                            @Override
                            public void onSuccess(EventLogResponse response)
                            {
                                mFileManager.deleteLogFile(response.getBundleId());
                                finishUpload();
                            }

                            @Override
                            public void onError(DataManager.DataManagerError error)
                            {
                                finishUpload();
                            }

                            private void finishUpload()
                            {
                                //If uploads are finished
                                if (--mSendingLogsCount == 0)
                                {
                                    //If there are currently logs, set timer, else clear old timer
                                    if (!sEventLogBundles.isEmpty() || mFileManager.getLogFileList().length > 0)
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

    private void addMixPanelProperties(JSONObject eventLogJson, Event event) throws JSONException
    {

        //Mixpanel tracking info in NOR-1016
        eventLogJson.put("context", event.getEventContext());
        eventLogJson.put("session_event_count", event.getSessionEventCount());
        eventLogJson.put("session_id", event.getSessionId());
        eventLogJson.put("platform", "android");
        eventLogJson.put("client", "android");
        eventLogJson.put("mobile", 1);

        User user = mUserManager.getCurrentUser();
        if (user != null)
        {
            eventLogJson.put("email", user.getEmail());
            eventLogJson.put("name", user.getFirstName() + " " + user.getLastName());
            eventLogJson.put("user_id", user.getId());
            eventLogJson.put("user_logged_in", 1);
        }
        else
        {
            eventLogJson.put("user_logged_in", 0);
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
