package com.handybook.handybook.core;

import android.os.Handler;
import android.os.Looper;

import com.crashlytics.android.Crashlytics;
import com.squareup.otto.Bus;

final class MainBus extends Bus {

    private final Handler mHandler = new Handler(Looper.getMainLooper());

    public MainBus() {
    }

    @Override
    public void unregister(final Object object) {
        /*
        quick-fix for crash caused by bus unregister:
        Caused by java.lang.IllegalArgumentException: Missing event handler for an annotated method
         */
        try {
            super.unregister(object);
        }
        catch (Exception e) {
            //want more information until we find the root cause
            Crashlytics.logException(e);
        }
    }

    @Override
    public final void register(final Object object) {
        if (Looper.myLooper() == Looper.getMainLooper()) { super.register(object); }
        else {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    MainBus.super.register(object);
                }
            });
        }
    }

    @Override
    public void post(final Object event) {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            super.post(event);
        }
        else {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    MainBus.super.post(event);
                }
            });
        }
    }
}
