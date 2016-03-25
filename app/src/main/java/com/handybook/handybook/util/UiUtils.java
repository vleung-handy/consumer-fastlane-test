package com.handybook.handybook.util;

import android.animation.Animator;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;

public final class UiUtils
{
    public static void toggleKeyboard(Activity activity)
    {
        View currentFocus = activity.getCurrentFocus();
        if (currentFocus != null)
        {
            final InputMethodManager inputMethodManager =
                    (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
        }
    }

    public static void dismissKeyboard(Activity activity)
    {
        View currentFocus = activity.getCurrentFocus();
        if (currentFocus != null)
        {
            InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(currentFocus.getWindowToken(), 0);
        }
    }

    public static ViewGroup getParent(View view)
    {
        return (ViewGroup) view.getParent();
    }

    public static void removeView(View view)
    {
        ViewGroup parent = getParent(view);
        if (parent != null)
        {
            parent.removeView(view);
        }
    }

    public static void replaceView(View currentView, View newView)
    {
        ViewGroup parent = getParent(currentView);
        if (parent == null)
        {
            return;
        }
        final int index = parent.indexOfChild(currentView);
        removeView(currentView);
        removeView(newView);
        parent.addView(newView, index);
    }

    /**
     * Users the circular reveal animation for making a view visible
     *
     * @param view
     */
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public static void revealView(final View view)
    {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP)
        {
            view.setVisibility(View.VISIBLE);
            return;
        }
        int cx = (view.getWidth()) / 2;
        int cy = (view.getHeight()) / 2;

        int finalRadius = Math.max(view.getWidth(), view.getHeight()) / 2;

        Animator anim = ViewAnimationUtils.createCircularReveal(view, cx, cy, 0, finalRadius);
        view.setVisibility(View.VISIBLE);
        anim.start();

    }
}
