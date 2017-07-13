package com.handybook.handybook.core.data.callback;

import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;

import com.handybook.handybook.core.model.response.ErrorResponse;

import java.lang.ref.WeakReference;

/**
 * use this instead of Callback to prevent the onCallbackSuccess/onCallbackError methods from being triggered
 * when it is cancelled, or the given fragment is in a bad state (detached, no activity, not added)
 * <p>
 *
 * @param <T>
 */
public abstract class Retrofit2FragmentSafeCallback<T extends ErrorResponse>
        extends HandyRetrofit2Callback<T> {

    /**
     * NOTE: for the WeakReference to effectively avoid memory leaks,
     * this callback should be used as a static class;
     * otherwise this will still have a strong reference
     */
    private WeakReference<Fragment> mFragmentWeakReference;

    public Retrofit2FragmentSafeCallback(@NonNull Fragment fragment) {
        mFragmentWeakReference = new WeakReference<>(fragment);
    }

    @Override
    public boolean areCallbacksEnabled() {
        return mFragmentWeakReference.get() != null
               && mFragmentWeakReference.get().getActivity() != null
               && !mFragmentWeakReference.get().isDetached()
               && mFragmentWeakReference.get().isAdded()
               && !mFragmentWeakReference.get().isRemoving();
    }
}
