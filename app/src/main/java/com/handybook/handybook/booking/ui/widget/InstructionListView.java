package com.handybook.handybook.booking.ui.widget;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.handybook.handybook.R;
import com.handybook.handybook.booking.model.BookingInstruction;
import com.handybook.handybook.booking.model.Instructions;
import com.handybook.handybook.library.ui.view.DragAndDropVerticalLinearLayout;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class InstructionListView extends FrameLayout
{

    Instructions mInstructions;
    private ArrayList<BookingInstructionView> mBookingInstructionViews;
    private OnInstructionsChangedListener mOnInstructionsChangedListener;
    private DragAndDropVerticalLinearLayout.OnChildrenSwappedListener mOnChildrenSwappedListener;
    private DragAndDropVerticalLinearLayout.OnChildMovedListener mOnChildMovedListener;

    private ScrollView mParentScrollView;

    private BookingInstructionView.OnStateChangedListener mInstructionStateListener;
    @Bind(R.id.preferences_container)
    DragAndDropVerticalLinearLayout mDnDLinearLayout;
    @Bind(R.id.title)
    TextView mTitle;

    {
        mInstructionStateListener = new BookingInstructionView.OnStateChangedListener()
        {
            @Override
            public void onStateChanged(final BookingInstruction bookingInstruction)
            {
                notifyObserver(ChangeType.STATE_CHANGE);
            }
        };
        mOnChildMovedListener = new DragAndDropVerticalLinearLayout.OnChildMovedListener()
        {
            @Override
            public void onChildMoved(final View child, final int fromPosition, final int toPosition)
            {
                //TODO: Move the BookingInstruction too!
                notifyObserver(ChangeType.POSITION_CHANGE);
            }
        };
        mOnChildrenSwappedListener = new DragAndDropVerticalLinearLayout.OnChildrenSwappedListener()
        {
            @Override
            public void onChildrenSwapped(
                    final View childA,
                    final int positionA,
                    final View childB,
                    final int positionB
            )
            {
                final List<BookingInstruction> bookingInstructions = mInstructions.getBookingInstructions();
                BookingInstruction biA = ((BookingInstructionView) childA).getBookingInstruction();
                BookingInstruction biB = ((BookingInstructionView) childB).getBookingInstruction();
                int realPosA = bookingInstructions.indexOf(biA);
                int realPosB = bookingInstructions.indexOf(biB);
                if (realPosA < realPosB)
                {// Let's not duck up the order
                    bookingInstructions.remove(realPosB);
                    bookingInstructions.remove(realPosA);
                    bookingInstructions.add(realPosA, biB);
                    bookingInstructions.add(realPosB, biA);
                }
                else
                {
                    bookingInstructions.remove(realPosA);
                    bookingInstructions.remove(realPosB);
                    bookingInstructions.add(realPosB, biA);
                    bookingInstructions.add(realPosA, biB);
                }
                notifyObserver(ChangeType.POSITION_CHANGE);
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
        mDnDLinearLayout.seOnChildrenSwappedListener(mOnChildrenSwappedListener);
        mDnDLinearLayout.setOnChildMovedListener(mOnChildMovedListener);

    }

    private void inflateAndBind()
    {
        LayoutInflater.from(getContext()).inflate(R.layout.widget_instruction_list_view, this, true);
        ButterKnife.bind(this);

    }

    public void reflect(@Nullable final Instructions instructions)
    {
        if (mBookingInstructionViews == null)
        {
            mBookingInstructionViews = new ArrayList<>();
        }
        else
        {
            mBookingInstructionViews.clear();

        }
        mDnDLinearLayout.removeAllViews();
        if (instructions == null)
        {
            return;
        }

        mInstructions = instructions;

        if (mInstructions.getBookingInstructions() != null)
        {
            mTitle.setVisibility(VISIBLE);
            mDnDLinearLayout.setVisibility(VISIBLE);
            for (BookingInstruction bookingInstruction : instructions.getBookingInstructions())
            {
                final BookingInstructionView bookingInstructionView = new BookingInstructionView(getContext());
                mBookingInstructionViews.add(bookingInstructionView);
                bookingInstructionView.reflect(bookingInstruction);
                bookingInstructionView.setOnStateChangedListener(mInstructionStateListener);
                mDnDLinearLayout.addView(bookingInstructionView);
            }
        }
        else
        {
            mTitle.setVisibility(GONE);
            mDnDLinearLayout.setVisibility(GONE);
        }
    }

    public void setOnInstructionsChangedListener(@NonNull OnInstructionsChangedListener listener)
    {
        mOnInstructionsChangedListener = listener;
    }


    private void notifyObserver(ChangeType changeType)
    {
        if (mOnInstructionsChangedListener != null)
        {
            mOnInstructionsChangedListener.onInstructionsChanged(mInstructions, changeType);
        }
    }

    public void setParentScrollContainer(final ScrollView scrollView)
    {
        mParentScrollView = scrollView;
        mDnDLinearLayout.linkScrollView(mParentScrollView);
    }

    public interface OnInstructionsChangedListener
    {
        void onInstructionsChanged(Instructions instructions, ChangeType changeType);
    }


    public enum ChangeType
    {
        STATE_CHANGE,
        POSITION_CHANGE,
        UNKNOWN
    }
}
