package com.handybook.handybook.manager;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.crashlytics.android.Crashlytics;
import com.handybook.handybook.constant.PrefsKey;
import com.handybook.handybook.data.SecurePreferences;

import javax.inject.Inject;

/**
 * Manager that handles CRUD operations on SharedPreferences
 * <p>
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
     * <p>
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
        if (prefs.containsKey(prefsKey.getKey()))
        {
            try
            {
                return prefs.getString(prefsKey.getKey());
            }
            catch (Exception e)
            {
                Crashlytics.logException(e);
                e.printStackTrace();
            }
        }
        return defaultValue;
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
     * Get a <p>boolean</p> from SecurePreferences or <b>defaultValue</b> if the key doesn't exist.
     *
     * @param prefsKey     the key that will be used
     * @param defaultValue value to be returned if key doesn't exist
     * @return Requested value or defaultValue
     */
    public boolean getBoolean(@NonNull final PrefsKey prefsKey, final boolean defaultValue)
    {
        if (prefs.containsKey(prefsKey.getKey()))
        {
            try
            {
                return Boolean.valueOf(prefs.getString(prefsKey.getKey()));
            }
            catch (Exception e)
            {
                Crashlytics.logException(e);
                e.printStackTrace();
            }
        }
        return defaultValue;
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
     * Get an <b>int</b> value from SharedPreference
     *
     * @param prefsKey     the key that will be used to retrieve the value
     * @param defaultValue the value that that will be returned in case key doesn't exist
     * @return int value from SharedPreferences or defaultValue
     */
    public int getInt(@NonNull final PrefsKey prefsKey, final int defaultValue)
    {
        if (prefs.containsKey(prefsKey.getKey()))
        {
            try
            {
                return Integer.valueOf(prefs.getString(prefsKey.getKey()));
            }
            catch (Exception e)
            {
                Crashlytics.logException(e);
                e.printStackTrace();
            }
        }
        return defaultValue;
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
     *
     * @param prefsKey     the key used to retrieve the value
     * @param defaultValue value to be stored in SharedPreferences
     * @return the <b>long</b> value from SharedPreferences or defaultValue if key doesn't exist
     */
    public long getLong(PrefsKey prefsKey, long defaultValue)
    {
        if (prefs.containsKey(prefsKey.getKey()))
        {
            try
            {
                return Long.valueOf(prefs.getString(prefsKey.getKey()));
            }
            catch (Exception e)
            {
                Crashlytics.logException(e);
                e.printStackTrace();
            }

        }
        return defaultValue;
    }
}
