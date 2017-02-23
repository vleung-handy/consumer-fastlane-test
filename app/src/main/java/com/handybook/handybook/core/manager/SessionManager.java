package com.handybook.handybook.core.manager;

import android.support.annotation.NonNull;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

/**
 * Created by sng on 2/15/17.
 * This is used to keep a cache for the session only
 * The session lasts for 30 min of inactivity. It's in baseActivity
 */
public class SessionManager
{
    private static int SESSION_TIMEOUT_MS = 30 * 60 * 1000; //30 minutes
    private long mLastModifiedTimeMs;
    private final Map<String, Object> mCacheMap;

    @Inject
    public SessionManager()
    {
        mCacheMap = new HashMap<>();
        markActivity();
    }

    public void markActivity()
    {
        mLastModifiedTimeMs = System.currentTimeMillis();
    }

    public boolean isSessionExpired()
    {
        return (System.currentTimeMillis() - mLastModifiedTimeMs) > SESSION_TIMEOUT_MS;
    }

    public void putToSessionCache(@NonNull String key, @NonNull Object obj)
    {
        clearCacheIfExpired();
        mCacheMap.put(key, obj);
    }

    public Object getFromSessionCache(@NonNull String key)
    {
        clearCacheIfExpired();

        return mCacheMap.get(key);
    }

    private void clearCacheIfExpired()
    {
        if (isSessionExpired())
        {
            mCacheMap.clear();
        }
    }
}
