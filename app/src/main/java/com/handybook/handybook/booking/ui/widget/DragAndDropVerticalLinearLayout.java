package com.handybook.handybook.booking.ui.widget;

import android.annotation.TargetApi;
import android.content.ClipData;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.util.SparseArray;
import android.view.DragEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ScrollView;

public class DragAndDropVerticalLinearLayout extends LinearLayout
{
    public static final int ABOVE = -1;
    public static final int BELOW = 1;
    public static String CLASS_TAG = DragAndDropVerticalLinearLayout.class.getSimpleName();
    /**
     * Linked scrollview to be scrolled while dragging items
     */
    private ScrollView mScrollView;
    private SparseArray<View> mDraggableChildren = new SparseArray<>();
    private OnLongClickListener mLongClickListener = new OnChildLongClickListener();
    private boolean mIsDragging;
    private View mViewBeingDragged;
    private boolean mIsDraggingEnabled;
    private DragShadowBuilder mShadowBuilder;


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
        Log.v(CLASS_TAG, "Enabling drag and drop");
        mIsDraggingEnabled = true;
        getRootView().setOnDragListener(new OnDragListener()
        {
            @Override
            public boolean onDrag(final View v, final DragEvent event)
            {
                Log.v(CLASS_TAG, "DragEvent: " + event.toString());
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
                        // Dropped, reassign View to ViewGroup
                        mViewBeingDragged.setVisibility(VISIBLE);
                        break;
                    case DragEvent.ACTION_DRAG_ENDED:
                        // Dropped, reassign View to ViewGroup
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
                Log.v(CLASS_TAG, "MotionEvent: " + event.toString());
                return false;
            }
        });
    }

    private void updateGhostLocation(final float x, final float y)
    {
        Log.v(CLASS_TAG, "updateGhostLocation(" + x + "," + y + ")");
        if (isAboveUpperBoundary(y))
        {
            swapChildren(getNeighbor(mViewBeingDragged, ABOVE), mViewBeingDragged);
        }
        else if (isBelowLowerBoundary(y))
        {
            swapChildren(mViewBeingDragged, getNeighbor(mViewBeingDragged, BELOW));
        }
    }

    private boolean isAboveUpperBoundary(final float y)
    {
        View neighborAbove = getNeighbor(mViewBeingDragged, ABOVE);
        if (neighborAbove == null)
        {
            return false;
        }
        return neighborAbove.getY() + neighborAbove.getHeight() / 2 < y;
    }

    private boolean isBelowLowerBoundary(final float y)
    {
        View neighborBelow = getNeighbor(mViewBeingDragged, BELOW);
        return neighborBelow.getY() + neighborBelow.getHeight() / 2 < y;
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
        Log.v(CLASS_TAG, "Setting all children draggable");
        for (int i = 0; i < getChildCount(); i++)
        {
            setChildDraggable(getChildAt(i), isDraggable);
        }
    }

    private void setChildDraggable(final View childView, final boolean setDraggable)
    {
        Log.v(CLASS_TAG, "Setting a child (" + childView + ") draggable");
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
    }

    public void moveChild(final int fromIndex, final int toIndex)
    {
        Log.v(CLASS_TAG, "moveChild(" + fromIndex + ", " + toIndex + ")");
        //TODO: Implement
    }

    public void swapChildren(final View childA, final View childB)
    {
        Log.v(CLASS_TAG, "swapChildren(" + childA + ", " + childB + ")");
        int positionA = indexOfChild(childA);
        int positionB = indexOfChild(childB);
        swapChildren(positionA, positionB);
    }

    public void swapChildren(final int positionA, final int positionB)
    {
        Log.v(CLASS_TAG, "swapChildren(" + positionA + ", " + positionB + ")");
        if (positionA == positionB || positionA < 0 || positionB < 0
                || positionA >= getChildCount() || positionB >= getChildCount())
        {
            return;
        }
        View viewA, viewB;
        viewA = getChildAt(positionA);
        viewB = getChildAt(positionB);
        removeView(viewA);
        removeView(viewB);
        super.addView(viewB, positionA);
        super.addView(viewA, positionB);
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

    private class OnChildLongClickListener implements OnLongClickListener
    {
        @Override
        public boolean onLongClick(final View view)
        {
            initiateDragging(view);
            return false;
        }
    }
}
