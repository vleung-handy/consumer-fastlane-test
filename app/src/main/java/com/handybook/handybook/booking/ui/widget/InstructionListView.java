package com.handybook.handybook.booking.ui.widget;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.handybook.handybook.R;
import com.handybook.handybook.booking.model.BookingInstruction;
import com.handybook.handybook.booking.model.Instructions;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

public class InstructionListView extends FrameLayout
{

    Instructions mInstructions;
    private ArrayList<BookingInstructionView> mBookingInstructionViews;
    private OnInstructionsChangedListener mOnInstructionsChangedListener;

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
                onInstructionStateChanged(bookingInstruction);
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
        if (mInstructions.getTitle() != null)
        {
            mTitle.setVisibility(VISIBLE);
            mTitle.setText(instructions.getTitle());
        }
        else
        {
            mTitle.setVisibility(GONE);
        }
        if (mInstructions.getBookingInstructions() != null)
        {
            mDnDLinearLayout.setVisibility(VISIBLE);
            for (BookingInstruction BookingInstruction : instructions.getBookingInstructions())
            {
                final BookingInstructionView bookingInstructionView = new BookingInstructionView(getContext());
                mBookingInstructionViews.add(bookingInstructionView);
                bookingInstructionView.reflect(BookingInstruction);
                bookingInstructionView.setOnStateChangedListener(mInstructionStateListener);
                mDnDLinearLayout.addView(bookingInstructionView);
            }
        }
        else
        {
            mDnDLinearLayout.setVisibility(GONE);
        }
    }

    public void setOnInstructionsChangedListener(@NonNull OnInstructionsChangedListener listener)
    {
        mOnInstructionsChangedListener = listener;
    }

    private void onInstructionStateChanged(final BookingInstruction BookingInstruction)
    {
        //TODO: If unchecked then animate to top of unchecked instructions (they group at the bottom)
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

    public void setParentScrollContainer(final ScrollView scrollView)
    {
        mParentScrollView = scrollView;
        mDnDLinearLayout.linkScrollView(mParentScrollView);
    }

    public interface OnInstructionsChangedListener
    {
        void onInstructionsChanged(Instructions instructions);
    }
}
