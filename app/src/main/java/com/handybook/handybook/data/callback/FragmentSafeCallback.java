package com.handybook.handybook.data.callback;

import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;

import java.lang.ref.WeakReference;

/**
 * use this instead of Callback to prevent the onCallbackSuccess/onCallbackError methods from being triggered
 * when it is cancelled, or the given fragment is in a bad state (detached, no activity, not added)
 * <p>
 *
 * @param <T>
 */
public abstract class FragmentSafeCallback<T> extends CancellableCallback<T>
{
    private WeakReference<Fragment> mFragmentWeakReference;

    public FragmentSafeCallback(
            @NonNull Fragment fragment
    )
    {
        mFragmentWeakReference = new WeakReference<>(fragment);
    }

    @Override
    protected boolean areCallbacksEnabled()
    {
        return super.areCallbacksEnabled()
                && mFragmentWeakReference.get() != null
                && !mFragmentWeakReference.get().isDetached() && mFragmentWeakReference.get()
                                                                                       .isAdded();
    }
}
