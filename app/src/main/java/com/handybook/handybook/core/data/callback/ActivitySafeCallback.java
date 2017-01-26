package com.handybook.handybook.core.data.callback;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Build;
import android.support.annotation.NonNull;

import java.lang.ref.WeakReference;

/**
 * use this instead of Callback to prevent the onCallbackSuccess/onCallbackError methods from being
 * triggered when it is cancelled, or the given activity is in a bad state (finishing, destroyed)
 * <p>
 *
 * @param <T>
 */
public abstract class ActivitySafeCallback<T, A extends Activity> extends CancellableCallback<T>
{
    /**
     * NOTE: for the WeakReference to effectively avoid memory leaks,
     * this callback should be used as a static class;
     * otherwise this will still have a strong reference
     */
    protected WeakReference<A> mActivityWeakReference;

    public ActivitySafeCallback(@NonNull A activity)
    {
        mActivityWeakReference = new WeakReference<>(activity);
    }

    @SuppressLint("NewApi")
    @Override
    protected boolean areCallbacksEnabled()
    {
        return super.areCallbacksEnabled()
                && mActivityWeakReference.get() != null
                && !mActivityWeakReference.get().isFinishing()
                && (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1 //< api 17
                || !mActivityWeakReference.get().isDestroyed());//requires api 17+
    }
}
