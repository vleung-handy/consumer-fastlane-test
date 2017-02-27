package com.handybook.handybook.core.manager;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.crashlytics.android.Crashlytics;
import com.handybook.handybook.core.constant.PrefsKey;
import com.handybook.handybook.core.data.SecurePreferences;

import javax.inject.Inject;

/**
 * TODO we should move away from SecurePreferences
 * because decrypt/encrypt throwing exceptions
 * and it is not that secure since the secret key is
 * in the config.properties file in plain text
 *
 * Manager that handles CRUD operations on SharedPreferences
 * <p>
 * (SharedPreferences is currently a SecurePreferences instance.)
 */
public class SecurePreferencesManager {

    private final SecurePreferences mSecurePreferences;

    @Inject
    public SecurePreferencesManager(final SecurePreferences prefs) {
        mSecurePreferences = prefs;
    }

    /**
     * Store a String value in shared preferences
     *<p/>
     * @param prefsKey the key used to store the value
     * @param value    the String value to be stored at the prefsKey location
     */
    public void setString(@NonNull final PrefsKey prefsKey, @Nullable final String value) {
        if (value == null) {
            Crashlytics.log("PrefsManager.setString() asked to store a null value!");
        }
        mSecurePreferences.put(prefsKey.getKey(), value);
    }

    /**
     * Get a String from SecurePreferences or <b>null</b> if the key doesn't exist.
     * <p>
     * See also the method {@link #getString(PrefsKey, String)}.
     *
     * @param prefsKey {@link com.handybook.handybook.core.constant.PrefsKey} to retrieve the value with.
     * @return Requested value or <b>null</b> if the key doesn't exist
     */
    @Nullable
    public String getString(@NonNull final PrefsKey prefsKey) {
        return getString(prefsKey, null);
    }

    /**
     * Get a <b>String</b> from SecurePreferences or the <b>defaultValue</b> if the key doesn't exist.
     *
     * @param prefsKey {@link com.handybook.handybook.core.constant.PrefsKey} to store the value at.
     * @param defaultValue {@link java.lang.String} to be returned if key doesn't exist.
     * @return Requested value or the defaultValue if the key doesn't exist
     */
    @Nullable
    public String getString(@NonNull final PrefsKey prefsKey, @Nullable final String defaultValue) {
        if (mSecurePreferences.containsKey(prefsKey.getKey())) {
            try {
                return mSecurePreferences.getString(prefsKey.getKey());
            }
            catch (Exception e) {
                Crashlytics.logException(e);
                e.printStackTrace();
            }
        }
        return defaultValue;
    }

    /**
     * Set a <b>boolean</b> value in SharedPreferences
     *
     * @param prefsKey {@link com.handybook.handybook.core.constant.PrefsKey} to store the value at.
     * @param value    the <b>int</b> value to be stored.
     */
    public void setBoolean(@NonNull final PrefsKey prefsKey, final boolean value) {
        mSecurePreferences.put(prefsKey.getKey(), Boolean.toString(value));
    }

    /**
     * Get a <p>boolean</p> from SecurePreferences or <b>defaultValue</b> if the key doesn't exist.
     *
     * @param prefsKey {@link com.handybook.handybook.core.constant.PrefsKey} to retrieve the value with.
     * @param defaultValue value to be returned if key doesn't exist
     * @return Requested value or defaultValue
     */
    public boolean getBoolean(@NonNull final PrefsKey prefsKey, final boolean defaultValue) {
        if (mSecurePreferences.containsKey(prefsKey.getKey())) {
            try {
                return Boolean.valueOf(mSecurePreferences.getString(prefsKey.getKey()));
            }
            catch (Exception e) {
                Crashlytics.logException(e);
                e.printStackTrace();
            }
        }
        return defaultValue;
    }

    /**
     * Store an <b>int</b> value in SharedPreferences
     *
     * @param prefsKey {@link com.handybook.handybook.core.constant.PrefsKey} to store the value at.
     * @param value    the value to be stored in SharedPreferences
     */
    public void setInt(@NonNull final PrefsKey prefsKey, final int value) {
        mSecurePreferences.put(prefsKey.getKey(), Integer.toString(value));
    }

    /**
     * Get an <b>int</b> value from SharedPreference
     *
     * @param prefsKey {@link com.handybook.handybook.core.constant.PrefsKey} to retrieve the value with.
     * @param defaultValue the value that that will be returned in case key doesn't exist
     * @return int value from SharedPreferences or defaultValue
     */
    public int getInt(@NonNull final PrefsKey prefsKey, final int defaultValue) {
        if (mSecurePreferences.containsKey(prefsKey.getKey())) {
            try {
                return Integer.valueOf(mSecurePreferences.getString(prefsKey.getKey()));
            }
            catch (Exception e) {
                Crashlytics.logException(e);
                e.printStackTrace();
            }
        }
        return defaultValue;
    }

    /**
     * Set a <b>long</b> value to SharedPreferences
     *
     * @param prefsKey {@link com.handybook.handybook.core.constant.PrefsKey} to store the value at.
     * @param value    the <b>long</b> value to be stored in SharedPreferences
     */
    public void setLong(@NonNull final PrefsKey prefsKey, final long value) {
        mSecurePreferences.put(prefsKey.getKey(), Long.toString(value));
    }

    /**
     * Get a <b>long</b> value from SharedPreferences
     *
     * @param prefsKey {@link com.handybook.handybook.core.constant.PrefsKey} to retrieve the value with.
     * @param defaultValue value to be stored in SharedPreferences
     * @return the <b>long</b> value from SharedPreferences or defaultValue if key doesn't exist
     */
    public long getLong(@NonNull final PrefsKey prefsKey, long defaultValue) {
        if (mSecurePreferences.containsKey(prefsKey.getKey())) {
            try {
                return Long.valueOf(mSecurePreferences.getString(prefsKey.getKey()));
            }
            catch (Exception e) {
                Crashlytics.logException(e);
                e.printStackTrace();
            }
        }
        return defaultValue;
    }

    /**
     * Remove the key and it's value if it exists.
     *
     * @param prefsKey {@link com.handybook.handybook.core.constant.PrefsKey} of the value to be removed
     */
    public void removeValue(@NonNull final PrefsKey prefsKey) {
        final String key = prefsKey.getKey();
        if (mSecurePreferences.containsKey(key)) {
            mSecurePreferences.removeValue(key);
        }
    }

    /**
     *
     * @param prefsKey
     * @return true if secure preferences contains key, otherwise false
     */
    public boolean containsValue(@NonNull final PrefsKey prefsKey) {
        return mSecurePreferences.containsKey(prefsKey.getKey());
    }

    public void clearAll() {
        mSecurePreferences.clear();
    }
}
