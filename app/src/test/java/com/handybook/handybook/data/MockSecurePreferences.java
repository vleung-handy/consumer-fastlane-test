package com.handybook.handybook.data;

import android.content.Context;

import com.handybook.handybook.core.data.SecurePreferences;

/**
 * Created by jwilliams on 3/4/15.
 */
public class MockSecurePreferences extends SecurePreferences {

    public MockSecurePreferences(
            Context context,
            String preferenceName,
            String secureKey,
            boolean encryptKeys
    ) throws SecurePreferencesException {
        super(context, preferenceName, secureKey, encryptKeys);
    }

    @Override
    protected void initCiphers(String secureKey) { }

    @Override
    public void put(String key, String value) {
    }

    @Override
    public String getString(String key) {
        return null;
    }

}
