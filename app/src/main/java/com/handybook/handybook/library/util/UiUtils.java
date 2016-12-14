package com.handybook.handybook.library.util;

import android.animation.Animator;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import android.os.Build;
import android.view.TouchDelegate;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;

public final class UiUtils
{
    /**
     * The amount of padding to extend the touch area of a view
     */
    public static final int SERVICE_ICON_TOUCH_PADDING = 200;

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
            InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(
                    Context.INPUT_METHOD_SERVICE);
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

    public static void extendTouchArea(
            final View parentContainer,
            final View viewToExtend,
            final int padArea
    )
    {
        parentContainer.post(new Runnable()
        {
            // Post in the parent's message queue to make sure the parent
            // lays out its children before you call getHitRect()
            @Override
            public void run()
            {
                // The bounds for the delegate view (an ImageButton
                // in this example)
                Rect delegateArea = new Rect();

                // The hit rectangle for the ImageButton
                viewToExtend.getHitRect(delegateArea);

                // Extend the touch area of the ImageButton beyond its bounds
                // on the right and bottom.
                delegateArea.right += padArea;
                delegateArea.bottom += padArea;
                delegateArea.left -= padArea;
                delegateArea.top -= padArea;

                // Instantiate a TouchDelegate.
                // "delegateArea" is the bounds in local coordinates of
                // the containing view to be mapped to the delegate view.
                // "myButton" is the child view that should receive motion
                // events.
                TouchDelegate touchDelegate = new TouchDelegate(delegateArea, viewToExtend);

                // Sets the TouchDelegate on the parent view, such that touches
                // within the touch delegate bounds are routed to the child.
                if (View.class.isInstance(viewToExtend.getParent()))
                {
                    ((View) viewToExtend.getParent()).setTouchDelegate(touchDelegate);
                }
            }
        });
    }
// TODO: uncomment when buildToolsVersion is updated to 25
    // This is a hack to disable shifting for BottomNavigationView
//    public static void removeShiftMode(BottomNavigationView view)
//    {
//        BottomNavigationMenuView menuView = (BottomNavigationMenuView) view.getChildAt(0);
//        try
//        {
//            Field shiftingMode = menuView.getClass().getDeclaredField("mShiftingMode");
//            shiftingMode.setAccessible(true);
//            shiftingMode.setBoolean(menuView, false);
//            shiftingMode.setAccessible(false);
//            for (int i = 0; i < menuView.getChildCount(); i++)
//            {
//                BottomNavigationItemView item = (BottomNavigationItemView) menuView.getChildAt(i);
//                item.setShiftingMode(true);
//                // set once again checked value, so view will be updated
//                item.setChecked(item.getItemData().isChecked());
//                // remove text field
//                item.getChildAt(1).setVisibility(View.GONE);
//            }
//        }
//        catch (NoSuchFieldException e)
//        {
//            Crashlytics.log("Unable to get shift mode field");
//        }
//        catch (IllegalAccessException e)
//        {
//            Crashlytics.log("Unable to change value of shift mode");
//        }
//    }
}
