package com.handybook.handybook.booking.ui.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.LinearLayout;

public class DragAndDropVerticalLinearLayout extends LinearLayout
{
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

}
