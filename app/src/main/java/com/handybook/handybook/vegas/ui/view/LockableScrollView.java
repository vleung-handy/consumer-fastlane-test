package com.handybook.handybook.vegas.ui.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ScrollView;

import com.handybook.handybook.R;

public class LockableScrollView extends ScrollView {

    private boolean mIsScrollingEnabled = true;

    public void setScrollingEnabled(final boolean scrollingEnabled) {
        mIsScrollingEnabled = scrollingEnabled;
    }

    public LockableScrollView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs);
    }

    public LockableScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public LockableScrollView(Context context) {
        super(context);
    }

    private void init(Context context, AttributeSet attrs) {
        final TypedArray ta = context
                .getTheme()
                .obtainStyledAttributes(attrs, R.styleable.LockableScrollView, 0, 0);

        try {
            setScrollingEnabled(ta.getBoolean(
                    R.styleable.LockableScrollView_scrollingEnabled,
                    true
            ));
        }
        finally {
            ta.recycle();
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return isScrollingEnabled() && super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        return isScrollingEnabled() && super.onTouchEvent(ev);
    }

    public boolean isScrollingEnabled() {
        return mIsScrollingEnabled;
    }
}
