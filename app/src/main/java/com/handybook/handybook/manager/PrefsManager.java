package com.handybook.handybook.manager;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

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

    /**
     * Set String in shared preference at prefsKey
     *
     * @param prefsKey key to store the value at
     * @param value    the String value to be stored at the prefsKey location
     */
    public void setString(@NonNull final PrefsKey prefsKey, @Nullable final String value)
    {
        prefs.put(prefsKey.getKey(), value);
    }

    /**
     * Get a String from SecurePreferences or null if key doesn't exist.
     * <p/>
     * See also the method {@link #getString(PrefsKey, String)}.
     * <p/>Returns **null** when value for _prefsKey_ not found.
     *
     * @param prefsKey PrefsKey key
     * @return Requested String or Null
     */
    @Nullable
    public String getString(@NonNull final PrefsKey prefsKey)
    {
        return getString(prefsKey, null);
    }

    /**
     * Get a String from SecurePreferences or _defaultValue_ if key doesn't exist.
     *
     * @param prefsKey     PrefsKey key
     * @param defaultValue Value returned if property at prefsKey doesn't exist
     * @return Requested String or defaultValue
     */
    @Nullable
    public String getString(@NonNull final PrefsKey prefsKey, @Nullable final String defaultValue)
    {
        if (!prefs.containsKey(prefsKey.getKey()))
        {
            return defaultValue;
        }
        return prefs.getString(prefsKey.getKey());
    }

    /**
     * Get a Boolean from SecurePreferences or _defaultValue_ if key doesn't exist
     *
     * @param prefsKey     PrefsKey key
     * @param defaultValue Value returned if property at prefsKey doesn't exist
     * @return Requested Boolean or defaultValue
     */
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
