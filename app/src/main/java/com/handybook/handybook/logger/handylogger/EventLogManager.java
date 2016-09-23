package com.handybook.handybook.logger.handylogger;


import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.crashlytics.android.Crashlytics;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.handybook.handybook.BuildConfig;
import com.google.gson.JsonSyntaxException;
import com.handybook.handybook.constant.PrefsKey;
import com.handybook.handybook.core.User;
import com.handybook.handybook.data.DataManager;
import com.handybook.handybook.logger.handylogger.model.Event;
import com.handybook.handybook.logger.handylogger.model.EventLogBundle;
import com.handybook.handybook.logger.handylogger.model.EventLogResponse;
import com.handybook.handybook.manager.SecurePreferencesManager;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

public class EventLogManager
{
    private static final String SENT_TIMESTAMP_SECS_KEY = "event_bundle_sent_timestamp";
    private static final int MAX_NUM_PER_BUNDLE = 10;
    private static final Gson GSON = new Gson();

    private static List<Event> sLogs = new ArrayList<>();
    private final Bus mBus;
    private final DataManager mDataManager;
    private final SecurePreferencesManager mSecurePreferencesManager;

    @Inject
    public EventLogManager(
            final Bus bus, final DataManager dataManager,
            final SecurePreferencesManager securePreferencesManager
    )
    {
        mBus = bus;
        mBus.register(this);
        mDataManager = dataManager;
        mSecurePreferencesManager = securePreferencesManager;
    }

    @Subscribe
    public synchronized void addLog(@NonNull LogEvent.AddLogEvent event)
    {
        if (!BuildConfig.DEBUG)
        {
            //log the payload to Crashlytics too
            try
            {
                //putting in try/catch block just in case GSON.toJson throws an exception
                String eventLogJson = GSON.toJson(event.getLog());
                String crashlyticsLogString =
                        event.getLog().getEventName()
                                + ": " + eventLogJson;
                Crashlytics.log(crashlyticsLogString);
            }
            catch (Exception e)
            {
                Crashlytics.logException(e);
            }

            sLogs.add(new Event(event.getLog()));
            if (sLogs.size() >= MAX_NUM_PER_BUNDLE)
            {
                saveLogs(null);
                sendLogs(null);
            }
        }
    }

    @Subscribe
    public synchronized void sendLogs(@Nullable final LogEvent.SendLogsEvent event)
    {
        final List<String> jsonBundleStrings = loadSavedEventBundles();
        if (jsonBundleStrings.size() == 0) { return; }
        for (final String bundleString : jsonBundleStrings)
        {
            final JsonObject eventLogBundle = GSON.fromJson(bundleString, JsonObject.class);
            eventLogBundle.addProperty(SENT_TIMESTAMP_SECS_KEY, System.currentTimeMillis() / 1000);
            mDataManager.postLogs(eventLogBundle, new DataManager.Callback<EventLogResponse>()
            {
                @Override
                public void onSuccess(EventLogResponse response)
                {
                    jsonBundleStrings.remove(bundleString);
                    saveToPreference(jsonBundleStrings);
                }

                @Override
                public void onError(DataManager.DataManagerError error) {}
            });
        }
    }

    @Subscribe
    public synchronized void saveLogs(@Nullable LogEvent.SaveLogsEvent event)
    {
        if (sLogs.size() > 0)
        {
            List<String> eventLogBundles = loadSavedEventBundles();
            eventLogBundles.add(GSON.toJson(new EventLogBundle(getUserId(), sLogs)));
            saveToPreference(eventLogBundles);
            sLogs = new ArrayList<>();
        }
    }

    private List<String> loadSavedEventBundles()
    {
        String json = mSecurePreferencesManager.getString(PrefsKey.EVENT_LOG_BUNDLES, "");
        String[] bundles = null;
        try
        {
            bundles = GSON.fromJson(json, String[].class);
        }
        catch (JsonSyntaxException e)
        {
            Crashlytics.logException(e);
        }
        if (bundles != null)
        {
            return new ArrayList<>(Arrays.asList(bundles));
        }
        else
        {
            return new ArrayList<>();
        }
    }

    private void saveToPreference(List<String> eventLogBundles)
    {
        String json = GSON.toJson(eventLogBundles);
        mSecurePreferencesManager.setString(PrefsKey.EVENT_LOG_BUNDLES, json);
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
}
