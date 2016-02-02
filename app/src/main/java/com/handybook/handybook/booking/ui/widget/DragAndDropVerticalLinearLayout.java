package com.handybook.handybook.booking.ui.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.ScrollView;

public class DragAndDropVerticalLinearLayout extends LinearLayout
{
    public static String CLASS_TAG = DragAndDropVerticalLinearLayout.class.getSimpleName();
    /**
     * Linked scrollview to be scrolled while dragging items
     */
    private ScrollView mScrollView;

    public DragAndDropVerticalLinearLayout(final Context context)
    {
        super(context);
        init();
    }

    public DragAndDropVerticalLinearLayout(final Context context, final AttributeSet attrs)
    {
        super(context, attrs);
        init();
    }

    public DragAndDropVerticalLinearLayout(final Context context, final AttributeSet attrs, final int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
        init();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public DragAndDropVerticalLinearLayout(final Context context, final AttributeSet attrs, final int defStyleAttr, final int defStyleRes)
    {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init()
    {
    }

    public void linkScrollView(final ScrollView scrollView)
    {
        mScrollView = scrollView;
    }

    public void moveChild( final int fromPosition, final int toPosition){
        Log.v(CLASS_TAG, "moveChild(" + fromPosition+", " + toPosition +")");
        //TODO: Implement
    }
    public void swapChildren( final int positionA, final int positionB){
        Log.v(CLASS_TAG, "swapChildren(" + positionA + ", " + positionB + ")");
        //TODO: Implement
    }
}
