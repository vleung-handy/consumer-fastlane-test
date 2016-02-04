package com.handybook.handybook.booking.ui.widget;

import android.animation.ObjectAnimator;
import android.annotation.TargetApi;
import android.content.ClipData;
import android.content.Context;
import android.os.Build;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.DragEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ScrollView;

public class DragAndDropVerticalLinearLayout extends LinearLayout
{
    public static String CLASS_TAG = DragAndDropVerticalLinearLayout.class.getSimpleName();

    public static final int ABOVE = -1;
    public static final int BELOW = 1;

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
    private DragShadowBuilder mShadowBuilder;
    private boolean mIsScrolling;
    private ObjectAnimator mScrollAnimator;


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
        enableDragAndDrop();
        setAllChildrenDraggable(true);
    }

    private void enableDragAndDrop()
    {
        //Log.v(CLASS_TAG, "Enabling drag and drop");
        mIsDraggingEnabled = true;
        getRootView().setOnDragListener(new OnDragListener()
        {
            @Override
            public boolean onDrag(final View view, final DragEvent event)
            {
                ////Log.v(CLASS_TAG, "DragEvent: " + event.toString());
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
                        //finishDragging();
                        break;
                    case DragEvent.ACTION_DROP:
                        // Dropped, reassign View to ViewGroup
                        finishDragging();
                        break;
                    case DragEvent.ACTION_DRAG_ENDED:
                        // Dropped, reassign View to ViewGroup
                        finishDragging();
                    default:
                        break;
                }
                return true;
            }
        });
        getRootView().setOnGenericMotionListener(new OnGenericMotionListener()
        {
            @Override
            public boolean onGenericMotion(final View v, final MotionEvent event)
            {
                //Log.v(CLASS_TAG, "MotionEvent: " + event.toString());
                return false;
            }
        });
    }

    private void initiateDragging(final View view)
    {
        mIsDragging = true;
        mViewBeingDragged = view;
        mShadowBuilder = new View.DragShadowBuilder(view);
        ClipData data = ClipData.newPlainText("", "");
        view.startDrag(data, mShadowBuilder, view, 0);
        view.setVisibility(View.INVISIBLE);
    }

    private void finishDragging()
    {
        mViewBeingDragged.setVisibility(VISIBLE);
        mIsDragging = false;
    }

    private void updateGhostLocation(final float x, final float y)
    {
        //Log.v(CLASS_TAG, "updateGhostLocation(" + x + "," + y + ")");
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
                //keep scrolling
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
            mScrollAnimator = ObjectAnimator.ofInt(mScrollView, "scrollY", min).setDuration(1000);
            mScrollAnimator.start();
            mIsScrolling = true;
        }
        else if (mBottomScrollZone.isTriggeredBy(x, y))
        {
            int max = mScrollView.getMaxScrollAmount();
            mScrollAnimator = ObjectAnimator.ofInt(mScrollView, "scrollY", max).setDuration(1000);
            mScrollAnimator.start();
            mIsScrolling = true;
        }
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
        if (neighborAbove == null)
        {
            return false;
        }
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
        //Log.v(CLASS_TAG, "Setting all children draggable");
        for (int i = 0; i < getChildCount(); i++)
        {
            setChildDraggable(getChildAt(i), isDraggable);
        }
    }

    private void setChildDraggable(final View childView, final boolean setDraggable)
    {
        //Log.v(CLASS_TAG, "Setting a child (" + childView + ") draggable");
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
        //Log.v(CLASS_TAG, "moveChild(" + fromIndex + ", " + toIndex + ")");
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
    }

    public void swapChildren(final View childA, final View childB)
    {
        //Log.v(CLASS_TAG, "swapChildren(" + childA + ", " + childB + ")");
        int positionA = indexOfChild(childA);
        int positionB = indexOfChild(childB);
        swapChildren(positionA, positionB);
    }

    public void swapChildren(final int positionA, final int positionB)
    {
        //Log.v(CLASS_TAG, "swapChildren(" + positionA + ", " + positionB + ")");
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
            removeViewAt(positionB);            // 0:K  1:L (2:M) 3:N   -> 0:K  1:K  2:M
            removeViewAt(positionA);            // 0:K (1:L) 2:N        -> 0:K  1:D
            super.addView(viewB, positionA);    // 0:K  1:N             -> 0:K (1:B) 2:D
            super.addView(viewA, positionB);    // 0:K  1:M (2:L) 3:N   -> 0:K  1:B (2:C) 3:D
        }
        else
        {
            removeViewAt(positionA);
            removeViewAt(positionB);
            super.addView(viewA, positionB);
            super.addView(viewB, positionA);

        }
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


    private static class ScrollZone
    {
        private static int SCROLL_ZONE_HEIGHT = 64;

        private final View mView;
        private final ScrollView mScrollView;
        private final ScrollZoneType mScrollZoneType;
        private float mTop, mRight, mBottom, mLeft;


        public enum ScrollZoneType
        {
            TOP, BOTTOM
        }


        public ScrollZone(
                @NonNull final View root,
                @NonNull final ScrollView scrollView,
                @NonNull ScrollZoneType type
        )
        {
            mView = root;
            mScrollView = scrollView;
            mScrollZoneType = type;
            update();
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
            mTop = Math.max(mScrollView.getScrollY() - mView.getTop(), 0);
            mBottom = mTop + SCROLL_ZONE_HEIGHT;
            mLeft = 0;
            mRight = mView.getWidth();
            //Log.v("TopScrollZone", this.toString());
        }

        private void updateBottom()
        {
            mBottom = Math.min(
                    mScrollView.getHeight() - mView.getY() + mScrollView.getScrollY(),
                    mView.getHeight());
            mTop = mBottom - SCROLL_ZONE_HEIGHT;
            mLeft = 0;
            mRight = mView.getWidth();
            //Log.v("BottomScrollZone", this.toString());
        }

        public boolean isTriggeredBy(final float x, final float y)
        {
            return x >= mLeft && x <= mRight && y >= mTop && y <= mBottom;
        }

        @Override
        public String toString()
        {
            return "ScrollZone:[top:" + mTop + ", right: " + mRight
                    + ", bottom:" + mBottom + ", left:" + mLeft + "]";
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
