package com.handybook.handybook.logger.handylogger.model;

import com.crashlytics.android.Crashlytics;
import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import com.handybook.handybook.constant.PrefsKey;
import com.handybook.handybook.manager.DefaultPreferencesManager;

import java.io.Serializable;

/**
 * Created by sng on 10/11/16.
 */

public class Session implements Serializable
{
    private static int SESSION_TIMEOUT_MS = 30 * 60 * 1000; //30 minutes
    private static final Gson GSON = new Gson();
    private int id;
    private int eventCount;
    private long lastModifiedTimeMs;

    private static Session mInstance;

    public static Session getInstance(DefaultPreferencesManager prefsManager)
    {
        if (prefsManager.contains(PrefsKey.LOG_SESSION))
        {
            String sessionStr = prefsManager.getString(PrefsKey.LOG_SESSION);
            try
            {
                mInstance = GSON.fromJson(sessionStr, Session.class);
                return mInstance;
            }
            catch (JsonParseException e)
            {
                //If there's a parse exception, delete the string
                prefsManager.removeValue(PrefsKey.LOG_SESSION);
                Crashlytics.log("Invalid Json: " + sessionStr);
            }
        }

        mInstance = new Session(prefsManager);
        return mInstance;
    }

    private Session(DefaultPreferencesManager prefsManager)
    {
        id = 0;
        eventCount = 0;
        saveSession(prefsManager);
    }

    public int getId()
    {
        return id;
    }

    public int getEventCount()
    {
        //Always start with count 1
        if (eventCount < 1) { eventCount = 1; }

        return eventCount;
    }

    public void incrementEventCount(DefaultPreferencesManager prefsManager)
    {
        //If greater then threshold then init a new session
        if (System.currentTimeMillis() - lastModifiedTimeMs > SESSION_TIMEOUT_MS)
        {
            //New session must increment the session id
            id++;
            //Reset event count on new sessions
            eventCount = 1;
        }
        else
        {
            //increment event count for current session
            eventCount++;
        }

        saveSession(prefsManager);
    }

    private void saveSession(DefaultPreferencesManager prefsManager)
    {
        //update last modified time
        lastModifiedTimeMs = System.currentTimeMillis();
        //Save the session on ever change
        prefsManager.setString(PrefsKey.LOG_SESSION, GSON.toJson(this));
    }
}
