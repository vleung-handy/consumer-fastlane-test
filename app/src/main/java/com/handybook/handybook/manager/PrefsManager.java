package com.handybook.handybook.manager;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.handybook.handybook.constant.PrefsKey;
import com.handybook.handybook.data.SecurePreferences;

import javax.inject.Inject;

/**
 * Manager that handles CRUD operations on SharedPreferences
 * <p/>
 * <p/>(SharedPreferences is currently a SecurePreferences instance.)
 */
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
     * Store a String value in shared preferences
     *
     * @param prefsKey the key used to store the value
     * @param value    the String value to be stored at the prefsKey location
     */
    public void setString(@NonNull final PrefsKey prefsKey, @Nullable final String value)
    {
        prefs.put(prefsKey.getKey(), value);
    }

    /**
     * Get a String from SecurePreferences or <b>null</b> if key doesn't exist.
     * <p/>
     * See also the method {@link #getString(PrefsKey, String)}.
     *
     * @param prefsKey {@link com.handybook.handybook.constant.PrefsKey} used to retrieve the value
     * @return Requested value or <b>null</b> if the key doesn't exist
     */
    @Nullable
    public String getString(@NonNull final PrefsKey prefsKey)
    {
        return getString(prefsKey, null);
    }

    /**
     * Get a tring</b>String from SecurePreferences or <b>defaultValue</b> if key doesn't exist.
     *
     * @param prefsKey     the key used to store the value
     * @param defaultValue value to be returned if key doesn't exist
     * @return Requested value or the defaultValue if the key doesn't exist
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
     * Get a <p>boolean</p> from SecurePreferences or <b>defaultValue</b> if the key doesn't exist.
     *
     * @param prefsKey     the key that will be used
     * @param defaultValue value to be returned if key doesn't exist
     * @return Requested value or defaultValue
     */
    public boolean getBoolean(@NonNull final PrefsKey prefsKey, final boolean defaultValue)
    {
        if (!prefs.containsKey(prefsKey.getKey()))
        {
            return defaultValue;
        }
        return Boolean.valueOf(prefs.getString(prefsKey.getKey()));
    }

    /**
     * Set a <b>boolean</b> value in SharedPreferences
     *
     * @param prefsKey the key that will be used
     * @param value    the value that will be stored
     */
    public void setBoolean(@NonNull final PrefsKey prefsKey, final boolean value)
    {
        prefs.put(prefsKey.getKey(), Boolean.toString(value));
    }

    /**
     * Store an <b>int</b> value in SharedPreferences
     *
     * @param prefsKey the key used to store the value
     * @param value    the value to be stored in SharedPreferences
     */
    public void setInt(@NonNull final PrefsKey prefsKey, final int value)
    {
        prefs.put(prefsKey.getKey(), Integer.toString(value));
    }

    /**
     * Get an <b>int</b> value from SharedPreference or return defultValue
     * <p/>
     * <p/>See also the method {@link #getInt(PrefsKey)}.
     *
     * @param prefsKey     the key that will be used to retrieve the value
     * @param defaultValue the value that that will be returned in case key doesn't exist
     * @return int value from SharedPreferences
     */
    public int getInt(@NonNull final PrefsKey prefsKey, final int defaultValue)
    {
        if (!prefs.containsKey(prefsKey.getKey()))
        {
            return defaultValue;
        }
        return getInt(prefsKey);
    }

    /**
     * Get an <b>int</b> value from SharedPreference
     * <p/>
     * Throws {@link java.lang.NumberFormatException} if key doesn't exist.
     * <p/>See also the method {@link #getInt(PrefsKey, int)}.
     *
     * @param prefsKey the key used to retrieve the value
     * @return int value from SharedPreferences
     */
    public int getInt(@NonNull final PrefsKey prefsKey)
    {
        return Integer.valueOf(prefs.getString(prefsKey.getKey()));
    }

    /**
     * Set a <b>long</b> value to SharedPreferences
     *
     * @param prefsKey the key used to store the value
     * @param value    the <b>long</b> value to be stored in SharedPreferences
     */
    public void setLong(@NonNull final PrefsKey prefsKey, final long value)
    {
        prefs.put(prefsKey.getKey(), Long.toString(value));
    }

    /**
     * Get a <b>long</b> value from SharedPreferences
     * <p/>
     * See also the method {@link #getLong(PrefsKey)}.
     *
     * @param prefsKey     the key used to retrieve the value
     * @param defaultValue value to be stored in SharedPreferences
     * @return the <b>long</b> value from SharedPreferences or defaultValue if key doesn't exist
     */
    public long getLong(PrefsKey prefsKey, long defaultValue)
    {
        if (!prefs.containsKey(prefsKey.getKey()))
        {
            return defaultValue;
        }
        return getLong(prefsKey);
    }

    /**
     * Get <b>long</b> value from SharedPreferences
     * <p/>
     * Throws {@link java.lang.NumberFormatException} if key doesn't exist.
     * <p/>See also the method {@link #getLong(PrefsKey, long)}.
     *
     * @param prefsKey the key used to retreive the value
     * @return the <b>long</b> value from SharedPreferences
     */
    public long getLong(@NonNull final PrefsKey prefsKey)
    {
        return Long.valueOf(prefs.getString(prefsKey.getKey()));
    }
}