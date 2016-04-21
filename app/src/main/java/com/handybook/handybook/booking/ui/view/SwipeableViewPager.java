package com.handybook.handybook.booking.ui.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.handybook.handybook.R;

/**
 * Created by jtse on 3/30/16.
 */
public class SwipeableViewPager extends ViewPager
{
    private boolean mSwipeable;

    public SwipeableViewPager(Context context)
    {
        super(context);
    }

    public SwipeableViewPager(Context context, AttributeSet attrs)
    {
        super(context, attrs);

        TypedArray a = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.SwipeableViewPager,
                0, 0);

        try
        {
            mSwipeable = a.getBoolean(R.styleable.SwipeableViewPager_swipeable, true);
        }
        finally
        {
            a.recycle();
        }

    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event)
    {
        // Never allow swiping to switch between pages
        if (!mSwipeable)
        {
            return false;
        }
        else
        {
            return super.onInterceptTouchEvent(event);
        }

    }

    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        // Never allow swiping to switch between pages
        if (!mSwipeable)
        {
            return false;
        }
        else
        {
            return super.onTouchEvent(event);
        }
    }
}
