package com.handybook.handybook.library.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

public class ViewPager extends android.support.v4.view.ViewPager {

    public ViewPager(final Context context) {
        super(context);
    }

    public ViewPager(final Context context, final AttributeSet attrs) {
        super(context, attrs);
    }

    /**
     * This allows the view pager to wrap its height
     * @param widthMeasureSpec
     * @param heightMeasureSpec
     */
    @Override
    protected void onMeasure(final int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int height = 0;
        for (int i = 0; i < getChildCount(); i++) {
            final View child = getChildAt(i);
            child.measure(
                    widthMeasureSpec,
                    MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED)
            );
            height = Math.max(child.getMeasuredHeight(), height);
        }

        heightMeasureSpec = MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }
}
