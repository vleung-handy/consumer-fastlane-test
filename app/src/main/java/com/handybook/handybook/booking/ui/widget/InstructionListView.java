package com.handybook.handybook.booking.ui.widget;

import android.content.ClipData;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.handybook.handybook.R;
import com.handybook.handybook.booking.model.ChecklistItem;
import com.handybook.handybook.booking.model.Instructions;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

public class InstructionListView extends FrameLayout
{

    Instructions mInstructions;
    private ArrayList<BookingInstructionView> mBookingInstructionViews;
    private OnInstructionsChangedListener mOnInstructionsChangedListener;
    private BookingInstructionView.OnStateChangedListener mInstructionStateListener;

    @Bind(R.id.preferences_container)
    ViewGroup mCheckListsLayout;
    @Bind(R.id.title)
    TextView mTitle;

    {
        mInstructionStateListener = new BookingInstructionView.OnStateChangedListener()
        {
            @Override
            public void onStateChanged(final ChecklistItem checklistItem)
            {
                onInstructionStateChanged(checklistItem);
            }
        };
    }

    public InstructionListView(final Context context)
    {
        super(context);
        init();
    }

    public InstructionListView(final Context context, final AttributeSet attrs)
    {
        super(context, attrs);
        init();
    }

    public InstructionListView(final Context context, final AttributeSet attrs, final int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init()
    {
        inflateAndBind();
    }

    private void inflateAndBind()
    {
        LayoutInflater.from(getContext()).inflate(R.layout.layout_instructions, this, true);
        ButterKnife.bind(this);
        getRootView().setOnDragListener(new OnDragListener()
        {
            @Override
            public boolean onDrag(final View v, final DragEvent event)
            {
                int action = event.getAction();
                switch (action)
                {
                    case DragEvent.ACTION_DRAG_STARTED:
                        // do nothing
                        break;
                    case DragEvent.ACTION_DRAG_ENTERED:
                        break;
                    case DragEvent.ACTION_DRAG_EXITED:
                        break;
                    case DragEvent.ACTION_DROP:
                        // Dropped, reassign View to ViewGroup
                        break;
                    case DragEvent.ACTION_DRAG_ENDED:
                        // Dropped, reassign View to ViewGroup
                    default:
                        break;
                }
                return true;
            }
        });
    }

    public void reflect(@Nullable final Instructions instructions)
    {
        if (instructions == null)
        {
            return;
        }
        if (mBookingInstructionViews == null)
        {
            mBookingInstructionViews = new ArrayList<>();
        }
        else
        {
            mBookingInstructionViews.clear();
        }

        mInstructions = instructions;
        if (mInstructions.getTitle() != null)
        {
            mTitle.setVisibility(VISIBLE);
            mTitle.setText(instructions.getTitle());
        }
        else
        {
            mTitle.setVisibility(GONE);
        }
        if (mInstructions.getChecklist() != null)
        {
            mCheckListsLayout.setVisibility(VISIBLE);
            for (ChecklistItem checklistItem : instructions.getChecklist())
            {
                final BookingInstructionView bookingInstructionView = new BookingInstructionView(getContext());
                mBookingInstructionViews.add(bookingInstructionView);
                bookingInstructionView.reflect(checklistItem);
                bookingInstructionView.setOnStateChangedListener(mInstructionStateListener);
                bookingInstructionView.setOnLongClickListener(new OnLongClickListener()
                {
                    @Override
                    public boolean onLongClick(final View view)
                    {
                        //TODO: Implement dragging
                        DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(view);
                        ClipData data = ClipData.newPlainText("", "");
                        view.startDrag(data, shadowBuilder, view, 0);
                        view.setVisibility(View.INVISIBLE);
                        return false;
                    }
                });
                mCheckListsLayout.addView(bookingInstructionView);
            }
        }
        else
        {
            mCheckListsLayout.setVisibility(GONE);
        }
    }

    public void setOnInstructionsChangedListener(@NonNull OnInstructionsChangedListener listener)
    {
        mOnInstructionsChangedListener = listener;
    }

    private void onInstructionStateChanged(final ChecklistItem checklistItem)
    {
        notifyObserver();
    }

    private void notifyObserver()
    {
        if (mOnInstructionsChangedListener == null)
        {
            return;
        }
        else
        {
            mOnInstructionsChangedListener.onInstructionsChanged(mInstructions);
        }
    }

    public interface OnInstructionsChangedListener
    {
        void onInstructionsChanged(Instructions instructions);
    }
}
