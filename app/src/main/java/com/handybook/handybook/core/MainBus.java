package com.handybook.handybook.core;

import com.crashlytics.android.Crashlytics;

import org.greenrobot.eventbus.EventBus;

public class MainBus extends EventBus {

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
}
