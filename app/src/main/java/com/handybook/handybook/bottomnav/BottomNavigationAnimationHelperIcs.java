package com.handybook.handybook.bottomnav;

import android.support.design.internal.TextScale;
import android.support.transition.AutoTransition;
import android.support.transition.TransitionManager;
import android.support.transition.TransitionSet;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.view.ViewGroup;

/**
 * this is copied from android.support.design.internal.BottomNavigationAnimationHelperIcs
 */
class BottomNavigationAnimationHelperIcs extends BottomNavigationAnimationHelperBase {

    private static final long ACTIVE_ANIMATION_DURATION_MS = 115L;

    private final TransitionSet mSet;

    BottomNavigationAnimationHelperIcs() {
        mSet = new AutoTransition();
        mSet.setOrdering(TransitionSet.ORDERING_TOGETHER);
        mSet.setDuration(ACTIVE_ANIMATION_DURATION_MS);
        mSet.setInterpolator(new FastOutSlowInInterpolator());
        TextScale textScale = new TextScale();
        mSet.addTransition(textScale);
    }

    void beginDelayedTransition(ViewGroup view) {
        TransitionManager.beginDelayedTransition(view, mSet);
    }
}
