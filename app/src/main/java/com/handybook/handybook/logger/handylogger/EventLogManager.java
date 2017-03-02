package com.handybook.handybook.logger.handylogger;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
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
import com.handybook.handybook.core.BaseApplication;
import com.handybook.handybook.core.User;
import com.handybook.handybook.core.UserManager;
import com.handybook.handybook.core.constant.PrefsKey;
import com.handybook.handybook.core.data.DataManager;
import com.handybook.handybook.core.manager.DefaultPreferencesManager;
import com.handybook.handybook.core.manager.FileManager;
import com.handybook.handybook.library.util.PropertiesReader;
import com.handybook.handybook.library.util.SystemUtils;
import com.handybook.handybook.logger.handylogger.model.Event;
import com.handybook.handybook.logger.handylogger.model.EventLog;
import com.handybook.handybook.logger.handylogger.model.EventLogBundle;
import com.handybook.handybook.logger.handylogger.model.EventLogResponse;
import com.handybook.handybook.logger.handylogger.model.EventSuperProperties;
import com.handybook.handybook.logger.handylogger.model.EventSuperPropertiesBase;
import com.handybook.handybook.logger.handylogger.model.Session;
import com.mixpanel.android.mpmetrics.MixpanelAPI;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import javax.inject.Inject;

public class EventLogManager {

    private static final String TAG = EventLogManager.class.getSimpleName();
    private static final int DEFAULT_USER_ID = -1;
    private static final String KEY_SENT_TIMESTAMP_SECS = "event_bundle_sent_timestamp";
    private static final int UPLOAD_TIMER_DELAY_MS = 60000; //1min
    private static final int UPLOAD_TIMER_DELAY_NO_INTERNET_MS = 15 * UPLOAD_TIMER_DELAY_MS; //15min
    static final int MAX_EVENTS_PER_BUNDLE = 50;

    private static final Gson GSON = new Gson();
    private static EventLogBundle sCurrentEventLogBundle;
    private final Context mContext;
    private final Bus mBus;
    private int mSendingLogsCount;
    private final DataManager mDataManager;
    private final FileManager mFileManager;
    private final DefaultPreferencesManager mPrefsManager;
    private MixpanelAPI mMixpanel;
    private Session mSession;
    private UserManager mUserManager; // Used just for mixed panel
    private boolean mIsUserLoggedIn; // This is used for updating mixpanel super property
    private Timer mTimer;

    @Inject
    public EventLogManager(
            final Context context,
            final Bus bus,
            final DataManager dataManager,
            final FileManager fileManager,
            final DefaultPreferencesManager prefsManager,
            final UserManager userManager
    ) {
        mContext = context;
        mBus = bus;
        mBus.register(this);
        mDataManager = dataManager;
        mFileManager = fileManager;
        mPrefsManager = prefsManager;
        mUserManager = userManager;
        //Send logs on initialization
        sendLogsOnInitialization();
        initMixPanel();
        //Session
        mSession = Session.getInstance(mPrefsManager);
    }

    @Subscribe
    public synchronized void addLog(@NonNull LogEvent.AddLogEvent event) {
        EventLog log = event.getLog();
        mSession.incrementEventCount(mPrefsManager);
        Event eventLog = new Event(log, mSession.getId(), mSession.getEventCount());

        //log the payload to Crashlytics too
        //Note: Should always log regardless of flavor/variant

        //Create upload timer when we get a new log and there isn't a timer currently
        if (mTimer == null) { setUploadTimer(); }

        //log the payload to Crashlytics too, useful for follow steps for debugging when crash
        try {
            //putting in try/catch block just in case GSON.toJson throws an exception
            //Get the log only to log
            JSONObject eventLogJson = new JSONObject(GSON.toJson(log));
            String logString = log.getEventName() + ": " + eventLogJson.toString();
            Crashlytics.log(logString);

            if (BuildConfig.FLAVOR.equals(BaseApplication.FLAVOR_PROD)) {
                //Mixpanel tracking info in NOR-1016
                addMixPanelProperties(eventLogJson, eventLog);
                mMixpanel.track(eventLog.getEventType(), eventLogJson);
            }
        }
        catch (JsonParseException | JSONException e) {
            Crashlytics.logException(e);
        }

        //If event log bundle is null or we've hit the max num per bundle then we create a new bundle
        if (sCurrentEventLogBundle == null ||
            sCurrentEventLogBundle.size() >= MAX_EVENTS_PER_BUNDLE) {
            //Create new event log bundle and add it to the List
            sCurrentEventLogBundle = new EventLogBundle(
                    getUserId(),
                    new ArrayList<Event>(),
                    Build.VERSION.RELEASE,
                    BuildConfig.VERSION_NAME,
                    SystemUtils.getDeviceId(mContext),
                    SystemUtils.getDeviceModel(),
                    mPrefsManager.getInstallationId()
            );
            synchronized (BundlesWrapper.class) {
                BundlesWrapper.BUNDLES.add(sCurrentEventLogBundle);
            }
        }

        //Save the EventLogBundle to preferences always
        //Must wrap the sCurrentEventLogBundle in this also, because the bundleWrapper might be in the process of
        //  converting the hash to JSON which also includes the sCurrentEventLogBundle
        synchronized (BundlesWrapper.class) {
            //If it's null here, it means that the current bundle was just sent. Reattempt to add log
            if (sCurrentEventLogBundle == null) {
                addLog(event);
                return;
            }

            //Prefix event_type with app_lib_
            eventLog.setEventType("app_lib_" + eventLog.getEventType());
            sCurrentEventLogBundle.addEvent(eventLog);

            saveToPreference(PrefsKey.EVENT_LOG_BUNDLES, BundlesWrapper.BUNDLES);
        }
    }

    /**
     * @return The list of Strings if returned, otherwise, null if nothing was saved in that pref
     * previously
     */
    private String loadSavedEventLogBundles(PrefsKey prefsKey) {
        synchronized (mPrefsManager) {
            return mPrefsManager.getString(prefsKey, null);
        }
    }

    /**
     * Save the List of EventLogBundles to the prefsKey
     */
    private void saveToPreference(PrefsKey prefsKey, List<EventLogBundle> eventLogBundles) {
        try {
            synchronized (mPrefsManager) {
                mPrefsManager.setString(prefsKey, GSON.toJson(eventLogBundles));
            }
        }
        //Concurrent modification could happen if the sCurrentEventLogBundle is modified while
        // GSON is turing the eventLogBundles into json. Hoping line 154 fix stops this from happening
        catch (JsonParseException | ConcurrentModificationException e) {
            //If there's an JsonParseException then clear the eventLogBundles because invalid json
            synchronized (BundlesWrapper.class) {
                BundlesWrapper.BUNDLES.clear();
            }
        }
    }

    private void removePreference(PrefsKey prefsKey) {
        synchronized (mPrefsManager) {
            mPrefsManager.removeValue(prefsKey);
        }
    }

    private int getUserId() {
        User user = mUserManager.getCurrentUser();
        if (user != null) {
            try {
                return Integer.parseInt(user.getId());
            }
            catch (Exception e) {
                return DEFAULT_USER_ID;
            }
        }
        return DEFAULT_USER_ID;
    }

    //************************************* handle all saving/sending of logs **********************
    private void setUploadTimer() {
        if (mTimer != null) {
            mTimer.cancel();
            mTimer = null;
        }

        mTimer = new Timer();
        mTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                sendLogsFromPreference();
            }
            //Check network connection and set timer delay appropriately
        }, hasNetworkConnection() ? UPLOAD_TIMER_DELAY_MS : UPLOAD_TIMER_DELAY_NO_INTERNET_MS);
    }

    private void sendLogsOnInitialization() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                //Check if there was logs that were to be sent but were never saved to file system
                String logBundles = loadSavedEventLogBundles(PrefsKey.EVENT_LOG_BUNDLES_TO_SEND);

                if (!TextUtils.isEmpty(logBundles)) {
                    //Save the previous ones to file system
                    saveLogsToFileSystem(logBundles);
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
    void sendLogsFromPreference() {
        String logBundles = loadSavedEventLogBundles(PrefsKey.EVENT_LOG_BUNDLES);

        if (!TextUtils.isEmpty(logBundles)) {
            synchronized (BundlesWrapper.class) {
                //Save the EventLogBundle to preferences always
                saveToPreference(
                        PrefsKey.EVENT_LOG_BUNDLES_TO_SEND,
                        BundlesWrapper.BUNDLES
                );
                BundlesWrapper.BUNDLES.clear();
            }
            sCurrentEventLogBundle = null;
            //delete the old one immediately
            removePreference(PrefsKey.EVENT_LOG_BUNDLES);
        }

        // We need to retrieve the logs previously and save them into the preference as send log key
        // clear out the existing variables for the log manager
        final String prefBundleString
                = loadSavedEventLogBundles(PrefsKey.EVENT_LOG_BUNDLES_TO_SEND);

        //This means nothing was stored previously in prefs
        if (!TextUtils.isEmpty(prefBundleString)) {
            String eventLogBundles = loadSavedEventLogBundles(PrefsKey.EVENT_LOG_BUNDLES_TO_SEND);
            //Save this to the file system and remove from original preference
            if (!TextUtils.isEmpty(eventLogBundles)) {
                saveLogsToFileSystem(eventLogBundles);
            }
        }

        sendLogs();
    }

    private synchronized void saveLogsToFileSystem(final String prefBundleString) {
        JsonObject[] eventLogBundles = GSON.fromJson(
                prefBundleString,
                JsonObject[].class
        );
        for (JsonObject logBundleJson : eventLogBundles) {
            String eventBundleId = logBundleJson.get(EventLogBundle.KEY_EVENT_BUNDLE_ID)
                                                .getAsString();
            boolean fileSaved = mFileManager.saveLogFile(eventBundleId, logBundleJson.toString());
            // If the file didn't save then we log an exception
            if (!fileSaved) {
                Crashlytics.logException(
                        new Exception(
                                "Failed to save log to file system: " + logBundleJson.toString()
                        )
                );
            }
        }

        //Remove preference the preference since it either saved or failed
        removePreference(PrefsKey.EVENT_LOG_BUNDLES_TO_SEND);
    }

    /**
     * handles sending the logs
     */
    private void sendLogs() {
        //This is jsut in case there's an invalid json file for some reason
        File invalidFile = null;

        try {
            File[] files = mFileManager.getLogFileList();
            if (files == null) {
                //Log exception
                Crashlytics.logException(new Exception(
                        "Log Files list returns null. Should not happen"));
                //Just return. next log event will trigger timer
                return;
            }

            mSendingLogsCount = files.length;
            for (final File file : files) {
                invalidFile = file;
                //Get each event log bundle
                JsonObject eventLogBundle = GSON.fromJson(
                        mFileManager.readFile(file),
                        JsonObject.class
                );
                if (eventLogBundle == null) {
                    mFileManager.deleteLogFile(file.getName());
                    continue;
                }
                //Add the sent timestamp value
                eventLogBundle.addProperty(
                        KEY_SENT_TIMESTAMP_SECS,
                        System.currentTimeMillis() / 1000
                );

                //Upload logs
                mDataManager.postLogs(
                        eventLogBundle,
                        new DataManager.Callback<EventLogResponse>() {
                            @Override
                            public void onSuccess(EventLogResponse response) {
                                mFileManager.deleteLogFile(response.getBundleId());
                                finishUpload();
                            }

                            @Override
                            public void onError(DataManager.DataManagerError error) {
                                finishUpload();
                            }

                            private void finishUpload() {
                                //If uploads are finished
                                if (--mSendingLogsCount == 0) {
                                    //If there are currently logs, set timer, else clear old timer
                                    if (!BundlesWrapper.BUNDLES.isEmpty()
                                        || mFileManager.getLogFileList().length > 0
                                            ) {
                                        setUploadTimer();
                                    }
                                    else {
                                        mTimer.cancel();
                                        mTimer = null;
                                    }
                                }
                            }
                        }
                );
            }
        }
        catch (JsonSyntaxException e) {
            Crashlytics.logException(e);
            Log.e(TAG, e.getMessage());
            //If there's json exception it means logs aren't valid and clear it out
            if (invalidFile != null) {
                mFileManager.deleteLogFile(invalidFile.getName());
            }
            //reset log count
            mSendingLogsCount = 0;
        }
    }

    // This should be called from BaseApplication after
    public void initMixPanel() {
        if (BuildConfig.FLAVOR.equals(BaseApplication.FLAVOR_STAGE)) { return; }

        //Set up mix panel
        String mixPanelProperty = BuildConfig.FLAVOR.equals(BaseApplication.FLAVOR_PROD)
                                  ? "mixpanel_api_key"
                                  : "mixpanel_api_key_internal";
        String mixpanelApiKey = PropertiesReader.getProperties(
                mContext,
                "config.properties"
        ).getProperty(mixPanelProperty);

        mMixpanel = MixpanelAPI.getInstance(mContext, mixpanelApiKey);

        //Set up super properties for mix panel
        JSONObject superProperties = null;
        try {
            superProperties = new JSONObject(GSON.toJson(new EventSuperPropertiesBase(
                    Build.VERSION.RELEASE,
                    BuildConfig.VERSION_NAME,
                    SystemUtils.getDeviceId(mContext),
                    SystemUtils.getDeviceModel(),
                    mPrefsManager.getInstallationId()
            )));
        }
        catch (JSONException e) {
            Crashlytics.logException(e);
        }

        if (mUserManager.isUserLoggedIn()) {
            mIsUserLoggedIn = true;
            //Only set this on initialization. Setting it after initialization will break mixpanel
            mMixpanel.identify(String.valueOf(getUserId()));
        }

        if (superProperties != null) { mMixpanel.registerSuperProperties(superProperties); }
    }

    private void addMixPanelUserSuperProperty() {

        //If user is not logged in, check if he's logged in
        if (!mIsUserLoggedIn) {
            //If logged in add user id to super properties
            if (mUserManager.isUserLoggedIn()) {
                try {
                    JSONObject userIdJson = new JSONObject();
                    userIdJson.put(EventSuperProperties.USER_ID, getUserId());
                    mMixpanel.registerSuperProperties(userIdJson);
                    mIsUserLoggedIn = true;
                }
                catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Mixpanel may contain extra info we don't log for internal logs
     * @param eventLogJson
     * @param event
     * @throws JSONException
     */
    private void addMixPanelProperties(JSONObject eventLogJson, Event event) throws JSONException {
        addMixPanelUserSuperProperty();

        //Mixpanel tracking info in NOR-1016
        eventLogJson.put("event_context", event.getEventContext());
        eventLogJson.put("session_event_count", event.getSessionEventCount());
        eventLogJson.put("session_id", event.getSessionId());
        eventLogJson.put("event_timestamp_ms", event.getTimestampMillis());
        eventLogJson.put("event_timestamp", event.getTimestampSecs());
        eventLogJson.put("event_id", event.getId());
        eventLogJson.put("platform", "android");
        eventLogJson.put("client", "android");
        eventLogJson.put("mobile", 1);

        User user = mUserManager.getCurrentUser();
        if (user != null) {
            eventLogJson.put("email", user.getEmail());
            eventLogJson.put("name", user.getFirstName() + " " + user.getLastName());
            eventLogJson.put("user_id", user.getId());
            eventLogJson.put("user_logged_in", 1);
            //This can be new when a new user is first created
            if (user.getAnalytics() != null) {
                eventLogJson.put("booking_count", user.getAnalytics().getTotalBookings());
            }
        }
        else {
            eventLogJson.put("user_logged_in", 0);
        }
    }

    private boolean hasNetworkConnection() {
        ConnectivityManager cm =
                (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }

    private static class BundlesWrapper {

        static final List<EventLogBundle> BUNDLES = new ArrayList<>();
    }
}
