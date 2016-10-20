package com.handybook.handybook.library.util;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.animation.Interpolator;

public class AnimationUtil
{

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public static void revealView(
            final View view,
            final View referenceView,
            final Interpolator interpolator,
            final long speed,
            AnimatorListenerAdapter mAnimatorListener
    )
    {
        int cx = (referenceView.getWidth()) / 2 + (int) referenceView.getX();
        int cy = (referenceView.getHeight()) / 2 + (int) referenceView.getY();

        int finalRadius = Math.max(view.getWidth(), view.getHeight()) / 2;

        Animator anim = ViewAnimationUtils.createCircularReveal(view, cx, cy, 0, finalRadius);
        if (mAnimatorListener != null)
        {
            anim.addListener(mAnimatorListener);
        }
        anim.setInterpolator(interpolator);
        if (speed > 0)
        {
            anim.setDuration(speed);
        }
        view.setVisibility(View.VISIBLE);
        anim.start();

    }
}
