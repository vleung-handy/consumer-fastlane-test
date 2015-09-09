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

    public String getString(PrefsKey prefsKey)
    {
        return getString(prefsKey, "");
    }

    public String getString(PrefsKey prefsKey, String defaultValue)
    {
        return prefs.getString(prefsKey.getKey());
        //return(prefs.getString(prefsKey.getKey(), defaultValue));
    }

    public boolean getBoolean(PrefsKey prefsKey, boolean defaultValue)
    {
      return Boolean.valueOf(prefs.getString(prefsKey.getKey()));
      //return(prefs.getBoolean(prefsKey.getKey(), defaultValue));
    }

    public void setBoolean(PrefsKey prefsKey, boolean value)
    {
        prefs.put(prefsKey.getKey(), Boolean.toString(value));
        //prefs.edit().putBoolean(prefsKey.getKey(), value).apply();
    }

    public void setString(PrefsKey prefsKey, String value)
    {
        prefs.put(prefsKey.getKey(), value);
        //prefs.edit().putString(prefsKey.getKey(), value).apply();
    }
}
