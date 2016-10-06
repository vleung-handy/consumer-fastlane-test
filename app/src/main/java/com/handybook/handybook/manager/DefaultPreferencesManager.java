package com.handybook.handybook.manager;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.handybook.handybook.constant.PrefsKey;

import javax.inject.Inject;

/**
 * before the creation of this class, almost all prefs were based on SecurePreferences
 * <p/>
 * created this so we can use default prefs rather than secure prefs for certain data because secure
 * prefs is causing a lot of issues (exceptions decrypting and encrypting), and not all prefs need
 * to be secure anyway
 * <p/>
 * <p/>
 * Manager that handles CRUD operations on default shared preferences
 **/
public class DefaultPreferencesManager
{
    private final SharedPreferences mDefaultSharedPreferences;

    @Inject
    public DefaultPreferencesManager(final Context context)
    {
        mDefaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public void setString(@NonNull final PrefsKey prefsKey, @Nullable final String value)
    {
        mDefaultSharedPreferences.edit().putString(prefsKey.getKey(), value).apply();
    }

    @Nullable
    public String getString(@NonNull final PrefsKey prefsKey)
    {
        return getString(prefsKey, null);
    }

    @Nullable
    public String getString(@NonNull final PrefsKey prefsKey, @Nullable final String defaultValue)
    {
        return mDefaultSharedPreferences.getString(prefsKey.getKey(), defaultValue);
    }

    public void setBoolean(@NonNull final PrefsKey prefsKey, final boolean value)
    {
        mDefaultSharedPreferences.edit().putBoolean(prefsKey.getKey(), value).apply();
    }

    public boolean getBoolean(@NonNull final PrefsKey prefsKey, final boolean defaultValue)
    {
        return mDefaultSharedPreferences.getBoolean(prefsKey.getKey(), defaultValue);
    }

    public void removeValue(@NonNull final PrefsKey prefsKey)
    {
        mDefaultSharedPreferences.edit().remove(prefsKey.getKey()).apply();
    }

    public void clearAll()
    {
        mDefaultSharedPreferences.edit().clear().apply();
    }
}
