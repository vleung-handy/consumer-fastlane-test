package com.handybook.handybook.library.ui.view;

import android.animation.LayoutTransition;
import android.animation.ObjectAnimator;
import android.annotation.TargetApi;
import android.content.ClipData;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Point;
import android.os.Build;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.DragEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import com.handybook.handybook.R;

public class DragAndDropVerticalLinearLayout extends LinearLayout
{
    public static final int LAYOUT_TRANSITION_DURATION = 125;
    public static final int ABOVE = -1;
    public static final int BELOW = 1;
    public static final int VIBRATE_TIME_MILLISECONDS = 15;
    public static String CLASS_TAG = DragAndDropVerticalLinearLayout.class.getSimpleName();
    /**
     * Linked scrollview to be scrolled while dragging items
     */
    private ScrollView mScrollView;
    private ScrollZone mTopScrollZone;
    private ScrollZone mBottomScrollZone;
    private SparseArray<View> mDraggableChildren = new SparseArray<>();
    private OnLongClickListener mLongClickListener = new OnChildLongClickListener();
    private boolean mIsDragging;
    private View mViewBeingDragged;
    private boolean mIsDraggingEnabled;
    private boolean mIsScrolling;
    private ObjectAnimator mScrollAnimator;
    private OnChildMovedListener mOnChildMovedListener;
    private OnChildrenSwappedListener mOnChildrenSwappedListener;

    public DragAndDropVerticalLinearLayout(final Context context)
    {
        super(context);
        init();
    }

    public DragAndDropVerticalLinearLayout(final Context context, final AttributeSet attrs)
    {
        super(context, attrs);
        init(attrs);
    }

    public DragAndDropVerticalLinearLayout(
            final Context context,
            final AttributeSet attrs,
            final int defStyleAttr
    )
    {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public DragAndDropVerticalLinearLayout(
            final Context context,
            final AttributeSet attrs,
            final int defStyleAttr,
            final int defStyleRes
    )
    {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(attrs);
    }

    private void init(final AttributeSet attrs)
    {
        TypedArray typedArray = getContext().obtainStyledAttributes(
                attrs,
                R.styleable.DragAndDropVerticalLinearLayout
        );
        try
        {
            mIsDraggingEnabled = typedArray.getBoolean(
                    R.styleable.DragAndDropVerticalLinearLayout_dragAndDropEnabled,
                    true
            );
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            typedArray.recycle();
        }
        init();
    }

    private void init()
    {
        initLayoutTransition();
        if (mIsDraggingEnabled)
        {
            enableDragAndDrop();
        }

    }

    private void initLayoutTransition()
    {
        LayoutTransition layoutTransition = getLayoutTransition();
        if (layoutTransition == null)
        {
            layoutTransition = new LayoutTransition();
        }
        layoutTransition.setDuration(LAYOUT_TRANSITION_DURATION);
        layoutTransition.setStartDelay(LayoutTransition.APPEARING, 1);
        layoutTransition.setStartDelay(LayoutTransition.DISAPPEARING, 1);
        layoutTransition.setStartDelay(LayoutTransition.CHANGE_APPEARING, 1);
        layoutTransition.setStartDelay(LayoutTransition.CHANGE_DISAPPEARING, 1);
    }

    public void enableDragAndDrop()
    {
        mIsDraggingEnabled = true;
        getRootView().setOnDragListener(new OnDragListener()
        {
            @Override
            public boolean onDrag(final View view, final DragEvent event)
            {
                int action = event.getAction();
                switch (action)
                {
                    case DragEvent.ACTION_DRAG_STARTED:
                        // do nothing
                        break;
                    case DragEvent.ACTION_DRAG_ENTERED:
                        break;
                    case DragEvent.ACTION_DRAG_LOCATION:
                        updateGhostLocation(event.getX(), event.getY());
                        break;
                    case DragEvent.ACTION_DRAG_EXITED:
                        break;
                    case DragEvent.ACTION_DROP:
                        finishDragging();
                        break;
                    case DragEvent.ACTION_DRAG_ENDED:
                        finishDragging();
                    default:
                        break;
                }
                return true;
            }
        });
        setAllChildrenDraggable(true);
    }

    public void disableDragAndDrop()
    {
        mIsDraggingEnabled = false;
        getRootView().setOnDragListener(null);
        setAllChildrenDraggable(false);
    }

    private void initiateDragging(final View view)
    {
        mIsDragging = true;
        mViewBeingDragged = view;
        DragShadowBuilder shadowBuilder = new DragShadowBuilder(view)
        {
            @Override
            public void onDrawShadow(final Canvas canvas)
            {
                canvas.rotate(2);
                view.draw(canvas);
            }

            @Override
            public void onProvideShadowMetrics(final Point shadowSize, final Point shadowTouchPoint)
            {
                shadowSize.set(view.getWidth() + 5, view.getHeight() + 25);
                shadowTouchPoint.set(shadowSize.x / 2, shadowSize.y / 2);
            }
        };
        ClipData data = ClipData.newPlainText("", "");
        view.startDrag(data, shadowBuilder, view, 0);
        view.setVisibility(View.INVISIBLE);
        vibrate();
    }

    private void finishDragging()
    {
        mViewBeingDragged.setVisibility(VISIBLE);
        mIsDragging = false;
        stopScrolling();
    }

    private void updateGhostLocation(final float x, final float y)
    {
        updateScrollView(x, y);
        if (shouldSwapWithViewAbove(y))
        {
            swapChildren(mViewBeingDragged, getNeighbor(mViewBeingDragged, ABOVE));
        }
        else if (shouldSwapWithViewBelow(y))
        {
            swapChildren(mViewBeingDragged, getNeighbor(mViewBeingDragged, BELOW));
        }
    }

    private void updateScrollView(final float x, final float y)
    {
        if (mScrollView == null)
        {
            return;
        }
        updateScrollZones(x, y);
        if (mTopScrollZone.isTriggeredBy(x, y) || mBottomScrollZone.isTriggeredBy(x, y))
        {
            if (mIsScrolling)
            {
                startScrolling(x, y);
            }
            else
            {
                startScrolling(x, y);
            }
        }
        else
        {
            stopScrolling();
        }

    }

    private void startScrolling(final float x, final float y)
    {
        if (mScrollAnimator != null && mScrollAnimator.isRunning())
        {
            mScrollAnimator.cancel();
        }
        if (mTopScrollZone.isTriggeredBy(x, y))
        {
            int min = 0;
            int d = mTopScrollZone.getDuration();
            mScrollAnimator = ObjectAnimator.ofInt(mScrollView, "scrollY", min).setDuration(d);
            mScrollAnimator.setStartDelay(0);
            mScrollAnimator.start();
            mIsScrolling = true;
        }
        else if (mBottomScrollZone.isTriggeredBy(x, y))
        {
            int max = mScrollView.getMaxScrollAmount();
            int d = mBottomScrollZone.getDuration();
            mScrollAnimator = ObjectAnimator.ofInt(mScrollView, "scrollY", max).setDuration(d);
            mScrollAnimator.setStartDelay(0);
            mScrollAnimator.start();
            mIsScrolling = true;
        }
    }


    private void updateScrollingAnimation(final float x, final float y)
    {
        if (mScrollAnimator == null)
        {
            return;
        }
        int duration = Integer.MAX_VALUE;
        if (mTopScrollZone.isTriggeredBy(x, y))
        {
            duration = mTopScrollZone.getDuration();
        }
        else if (mBottomScrollZone.isTriggeredBy(x, y))
        {
            duration = mBottomScrollZone.getDuration();
        }
        float fraction = mScrollAnimator.getAnimatedFraction();
        mScrollAnimator.setDuration(duration);
        mScrollAnimator.setCurrentPlayTime(Math.round(fraction * duration));
        mScrollAnimator.setStartDelay(0);
    }

    private void stopScrolling()
    {
        if (mScrollAnimator == null)
        {
            return;
        }
        mScrollAnimator.cancel();
        mIsScrolling = false;
    }

    private void updateScrollZones(final float x, final float y)
    {
        mTopScrollZone.update();
        mBottomScrollZone.update();
    }

    private boolean shouldSwapWithViewAbove(final float y)
    {
        View neighborAbove = getNeighbor(mViewBeingDragged, ABOVE);
        return neighborAbove != null && y < neighborAbove.getY() + neighborAbove.getHeight() / 2;
    }

    private boolean shouldSwapWithViewBelow(final float y)
    {
        View neighborBelow = getNeighbor(mViewBeingDragged, BELOW);
        return neighborBelow != null && y > neighborBelow.getY() + neighborBelow.getHeight() / 2;
    }

    private View getNeighbor(final View view, final int indexShift)
    {
        final int index = indexOfChild(view);
        if (index < 0)
        {
            return null;
        }
        return getChildAt(index + indexShift);
    }

    private void setAllChildrenDraggable(final boolean isDraggable)
    {
        for (int i = 0; i < getChildCount(); i++)
        {
            setChildDraggable(getChildAt(i), isDraggable);
        }
    }

    private void setChildDraggable(final View childView, final boolean setDraggable)
    {
        int indexOfChild = indexOfChild(childView);
        if (indexOfChild < 0)
        {
            return;
        }
        if (setDraggable)
        {
            childView.setOnLongClickListener(mLongClickListener);
            childView.setLongClickable(true);
            mDraggableChildren.put(indexOfChild, childView);
        }
        else
        {
            childView.setOnLongClickListener(null);
            childView.setLongClickable(false);
            mDraggableChildren.delete(indexOfChild);

        }
    }

    public void linkScrollView(final ScrollView scrollView)
    {
        mScrollView = scrollView;
        mTopScrollZone = new ScrollZone(this, mScrollView, ScrollZone.ScrollZoneType.TOP);
        mBottomScrollZone = new ScrollZone(this, mScrollView, ScrollZone.ScrollZoneType.BOTTOM);
    }

    public void moveChild(final int fromIndex, final int toIndex)
    {
        if (fromIndex == toIndex || fromIndex < 0 || toIndex < 0
                || fromIndex >= getChildCount() || toIndex >= getChildCount())
        {
            return;
        }
        final View view = getChildAt(fromIndex);
        removeViewAt(fromIndex);
        if (fromIndex < toIndex)
        {
            super.addView(view, toIndex - 1);
        }
        else
        {
            super.addView(view, toIndex);
        }
        notififyChildMovedListener(view, fromIndex, toIndex);
    }

    private void notififyChildMovedListener(
            final View child,
            final int fromPosition,
            final int toPosition
    )
    {
        if (mOnChildMovedListener == null)
        {
            return;
        }
        mOnChildMovedListener.onChildMoved(child, fromPosition, toPosition);
    }

    public void swapChildren(final View childA, final View childB)
    {
        int positionA = indexOfChild(childA);
        int positionB = indexOfChild(childB);
        if (positionA == positionB || positionA < 0 || positionB < 0
                || positionA >= getChildCount() || positionB >= getChildCount())
        {
            return;
        }
        View viewA, viewB;
        viewA = getChildAt(positionA);
        viewB = getChildAt(positionB);
        if (positionA < positionB)
        { // First fiddle with higher indexes, to not affect smaller one
            swapViews(positionB, viewB, positionA, viewA);
        }
        else
        {
            swapViews(positionA, viewA, positionB, viewB);
        }
        notifyChildrenSwappedListener(viewA, positionA, viewB, positionB);
        vibrate();
    }

    private void swapViews(final int positionA, final View viewA, final int positionB, final View viewB)
    {
        removeView(viewA);
        removeView(viewB);
        super.addView(viewA, positionB);
        super.addView(viewB, positionA);
    }

    /**
     * Added this because super.removeView for some reason sometimes didn't remove the view from the linearlayout.
     * Someone on slackoverflow noted that animations could cause a delay
     * @param view
     */
    @Override
    public void removeView(View view) {
        super.removeView(view);
        //Add this as a backup in case. Not sure if it'll 100% fix it. Not sure how to reproduce it
        ViewGroup parent = (ViewGroup) view.getParent();
        if (parent != null) {
            parent.removeView(view);
        }
    }

    private void vibrate()
    {
        Vibrator vibrator = (Vibrator) getContext().getSystemService(Context.VIBRATOR_SERVICE);
        if (vibrator.hasVibrator())
        {
            vibrator.vibrate(VIBRATE_TIME_MILLISECONDS);
        }
    }

    private void notifyChildrenSwappedListener(
            final View viewA,
            final int originalPositionOfA,
            final View viewB,
            final int originalPositionOfB
    )
    {
        if (mOnChildrenSwappedListener == null)
        {
            return;
        }
        mOnChildrenSwappedListener.onChildrenSwapped(
                viewA,
                originalPositionOfA,
                viewB,
                originalPositionOfB
        );
    }

    @Override
    public void addView(final View child)
    {
        super.addView(child);
        initView(child);
    }

    @Override
    public void addView(final View child, final int index)
    {
        super.addView(child, index);
        initView(child);
    }

    @Override
    public void addView(final View child, final int width, final int height)
    {
        super.addView(child, width, height);
        initView(child);
    }

    @Override
    public void addView(final View child, final ViewGroup.LayoutParams params)
    {
        super.addView(child, params);
        initView(child);
    }

    @Override
    public void addView(final View child, final int index, final ViewGroup.LayoutParams params)
    {
        super.addView(child, index, params);
        initView(child);
    }

    private void initView(final View child)
    {
        if (mIsDraggingEnabled)
        {
            setChildDraggable(child, true);
        }
    }

    public void setOnChildMovedListener(final OnChildMovedListener onChildMovedListener)
    {
        mOnChildMovedListener = onChildMovedListener;
    }

    public void seOnChildrenSwappedListener(final OnChildrenSwappedListener onChildrenSwappedListener)
    {
        mOnChildrenSwappedListener = onChildrenSwappedListener;
    }


    public interface OnChildMovedListener
    {
        void onChildMoved(View child, int fromPosition, int toPosition);
    }


    public interface OnChildrenSwappedListener
    {
        void onChildrenSwapped(View childA, int positionA, View childB, int positionB);
    }


    /**
     * A representation of rectangle which decides if the linked scrollView should scroll
     */
    private static class ScrollZone
    {
        private static int SCROLL_ZONE_HEIGHT = 256;
        private static int MIN_DURATION = 750;
        private static int DURATION_MULTIPLIER = 20;

        private final View mRoot;
        private final ScrollView mScrollView;
        private final ScrollZoneType mScrollZoneType;
        private float mTop, mRight, mBottom, mLeft, mLastX, mLastY;
        private boolean mIsTriggered;
        private int mDuration;

        {
            reset();
        }

        public ScrollZone(
                @NonNull final View root,
                @NonNull final ScrollView scrollView,
                @NonNull ScrollZoneType type
        )
        {
            mRoot = root;
            mScrollView = scrollView;
            mScrollZoneType = type;
            update();
        }

        private void reset()
        {
            mLastX = 0;
            mLastY = 0;
            mDuration = Integer.MAX_VALUE;
        }

        public int getDuration()
        {
            if (!mIsTriggered)
            {
                return mDuration;
            }
            switch (mScrollZoneType)
            {
                case TOP:
                    mDuration = Math.round(
                            MIN_DURATION + (
                                    DURATION_MULTIPLIER * MIN_DURATION * Math.min(
                                            Math.max((mLastY - mTop) / SCROLL_ZONE_HEIGHT, 0), 1
                                    ) //clamp!:)
                            )
                    );
                    break;
                case BOTTOM:
                    mDuration = Math.round(
                            MIN_DURATION + (
                                    DURATION_MULTIPLIER * MIN_DURATION * (1 - Math.min(
                                            Math.max((mLastY - mTop) / SCROLL_ZONE_HEIGHT, 0), 1)
                                    ) //clamp!:)
                            )
                    );
                    break;
            }
            return mDuration;
        }

        public void update()
        {
            switch (mScrollZoneType)
            {
                case TOP:
                    updateTop();
                    break;
                case BOTTOM:
                    updateBottom();
                    break;
            }
        }

        private void updateTop()
        {
            mTop = Math.max(mScrollView.getScrollY() - mRoot.getTop(), 0);
            mBottom = mTop + SCROLL_ZONE_HEIGHT;
            mLeft = 0;
            mRight = mRoot.getWidth();
        }

        private void updateBottom()
        {
            mBottom = Math.min(
                    mScrollView.getHeight() - mRoot.getY() + mScrollView.getScrollY(),
                    mRoot.getHeight());
            mTop = mBottom - SCROLL_ZONE_HEIGHT;
            mLeft = 0;
            mRight = mRoot.getWidth();
        }

        public boolean isTriggeredBy(final float x, final float y)
        {
            mLastX = x;
            mLastY = y;
            mIsTriggered = x >= mLeft && x <= mRight && y >= mTop && y <= mBottom;
            if (!mIsTriggered)
            {
                reset();
            }
            return mIsTriggered;
        }

        @Override
        public String toString()
        {
            return "ScrollZone:[top:" + mTop + ", right: " + mRight
                    + ", bottom:" + mBottom + ", left:" + mLeft + "]";
        }

        public enum ScrollZoneType
        {
            TOP, BOTTOM
        }
    }


    private class OnChildLongClickListener implements View.OnLongClickListener
    {
        @Override
        public boolean onLongClick(final View view)
        {
            initiateDragging(view);
            return false;
        }
    }
}
