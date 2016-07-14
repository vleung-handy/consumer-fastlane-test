package com.handybook.handybook.booking.ui.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.handybook.handybook.R;

/**
 *
 * This view pager offers the ability to enable/disable swiping
 *
 * Created by jtse on 3/30/16.
 */
public class SwipeableViewPager extends ViewPager
{
    private boolean mSwipeable;
    private boolean mWrapContentEnabled;

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
            mWrapContentEnabled = a.getBoolean(R.styleable.SwipeableViewPager_wrap_content_enabled, false);
        }
        finally
        {
            a.recycle();
        }
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        //if we want the view pager to work with wrapped content, then we need to do this.
        if (mWrapContentEnabled) {
            int height = 0;
            for(int i = 0; i < getChildCount(); i++) {
                View child = getChildAt(i);
                child.measure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
                int h = child.getMeasuredHeight();
                if(h > height) height = h;
            }

            heightMeasureSpec = MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY);
        }

        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
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
