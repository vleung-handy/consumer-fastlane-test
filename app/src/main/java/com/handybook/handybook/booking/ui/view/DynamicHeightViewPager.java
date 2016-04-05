package com.handybook.handybook.booking.ui.view;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;

/**
 * Created by jtse on 3/30/16.
 */
public class DynamicHeightViewPager extends ViewPager
{

    private static final String TAG = DynamicHeightViewPager.class.getName();

    public DynamicHeightViewPager(Context context)
    {
        super(context);
    }

    public DynamicHeightViewPager(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

//    @Override
//    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//
//        //there are no children to be measured.
//        if (getChildCount() == 0) {
//            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
//            return;
//        }
//
//        Log.d(TAG, "onMeasure: for fragment:" + getCurrentItem());
//        View child = getChildAt(getCurrentItem());
//        child.measure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
//        heightMeasureSpec = MeasureSpec.makeMeasureSpec(child.getMeasuredHeight(), MeasureSpec.EXACTLY);
//
//        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
//    }
}
