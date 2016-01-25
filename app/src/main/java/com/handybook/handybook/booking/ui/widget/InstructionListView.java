package com.handybook.handybook.booking.ui.widget;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
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
    ArrayList<InstructionView> mInstructionViews;

    @Bind(R.id.preferences_container)
    ViewGroup mCheckListsLayout;
    @Bind(R.id.title)
    TextView mTitle;

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
    }

    public void reflect(@Nullable final Instructions instructions)
    {
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

        if (mInstructions.getChecklist() != null)
        {
            mCheckListsLayout.setVisibility(VISIBLE);
            for (ChecklistItem checklistItem : instructions.getChecklist())
            {
                final InstructionView instructionView = new InstructionView(getContext());
                instructionView.reflect(checklistItem);
                mCheckListsLayout.addView(instructionView);
            }
        }
        else
        {
            mCheckListsLayout.setVisibility(GONE);
        }
    }

    public interface OnInstructionsChangedListener
    {
        void onInstructionsChanged(Instructions instructions);
    }
}
