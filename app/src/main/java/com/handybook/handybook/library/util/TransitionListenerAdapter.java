package com.handybook.handybook.library.util;

import android.os.Build;
import android.support.annotation.RequiresApi;
import android.transition.Transition;

@RequiresApi(api = Build.VERSION_CODES.KITKAT)
public abstract class TransitionListenerAdapter implements Transition.TransitionListener {

    @Override
    public void onTransitionStart(final Transition transition) {

    }

    @Override
    public void onTransitionEnd(final Transition transition) {

    }

    @Override
    public void onTransitionCancel(final Transition transition) {

    }

    @Override
    public void onTransitionPause(final Transition transition) {

    }

    @Override
    public void onTransitionResume(final Transition transition) {

    }
}
