package com.handybook.handybook.core.data.callback;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Build;
import android.support.annotation.NonNull;

import com.handybook.handybook.core.model.response.ErrorResponse;

import java.lang.ref.WeakReference;

/**
 * use this instead of Callback to prevent the onCallbackSuccess/onCallbackError methods from being
 * triggered when it is cancelled, or the given activity is in a bad state (finishing, destroyed)
 * <p>
 *
 * @param <T>
 */
public abstract class Retrofit2ActivitySafeCallback<T extends ErrorResponse, A extends Activity>
        extends HandyRetrofit2Callback<T> {

    /**
     * NOTE: for the WeakReference to effectively avoid memory leaks,
     * this callback should be used as a static class;
     * otherwise this will still have a strong reference
     */
    protected WeakReference<A> mActivityWeakReference;

    public Retrofit2ActivitySafeCallback(@NonNull A activity) {
        mActivityWeakReference = new WeakReference<>(activity);
    }

    @SuppressLint("NewApi")
    @Override
    protected boolean areCallbacksEnabled() {
        return mActivityWeakReference.get() != null
               && !mActivityWeakReference.get().isFinishing()
               && (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1 //< api 17
                   || !mActivityWeakReference.get().isDestroyed());//requires api 17+
    }
}
