package com.handybook.handybook.manager;

import com.handybook.handybook.constant.PrefsKey;
import com.handybook.handybook.data.SecurePreferences;

import javax.inject.Inject;

public class PrefsManager
{
    //UPGRADE: Need to update our secure prefs gradle

    private final SecurePreferences prefs;

    @Inject
    public PrefsManager(final SecurePreferences prefs)
    {
        this.prefs = prefs;
    }

    // String
    public void setString(PrefsKey prefsKey, String value)
    {
        prefs.put(prefsKey.getKey(), value);
    }

    public String getString(PrefsKey prefsKey)
    {
        return getString(prefsKey, "");
    }

    public String getString(PrefsKey prefsKey, String defaultValue)
    {
        if (!prefs.containsKey(prefsKey.getKey()))
        {
            return defaultValue;
        }
        return prefs.getString(prefsKey.getKey());
    }

    // boolean
    public boolean getBoolean(PrefsKey prefsKey, boolean defaultValue)
    {
        if (!prefs.containsKey(prefsKey.getKey()))
        {
            return defaultValue;
        }
        return Boolean.valueOf(prefs.getString(prefsKey.getKey()));
    }

    public void setBoolean(PrefsKey prefsKey, boolean value)
    {
        prefs.put(prefsKey.getKey(), Boolean.toString(value));
    }

    // int
    public void setInt(final PrefsKey prefsKey, final int intValue)
    {
        prefs.put(prefsKey.getKey(), Integer.toString(intValue));
    }

    public int getInt(final PrefsKey prefsKey, int defaultValue)
    {
        if (!prefs.containsKey(prefsKey.getKey()))
        {
            return defaultValue;
        }
        return getInt(prefsKey);
    }

    public int getInt(final PrefsKey prefsKey)
    {
        return Integer.valueOf(prefs.getString(prefsKey.getKey()));
    }


    // long
    public void setLong(final PrefsKey prefsKey, final long longValue)
    {
        prefs.put(prefsKey.getKey(), Long.toString(longValue));
    }

    public long getLong(PrefsKey prefsKey, long defaultValue)
    {
        if (!prefs.containsKey(prefsKey.getKey()))
        {
            return defaultValue;
        }
        return getLong(prefsKey);
    }

    public long getLong(PrefsKey prefsKey)
    {
        return Long.valueOf(prefs.getString(prefsKey.getKey()));
    }

}
