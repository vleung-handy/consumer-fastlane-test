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

import butterknife.Bind;
import butterknife.ButterKnife;

public class InstructionsLayout extends FrameLayout
{
    @Bind(R.id.preferences_container)
    ViewGroup mCheckListsLayout;
    @Bind(R.id.title)
    TextView mTitle;

    public InstructionsLayout(final Context context)
    {
        super(context);
    }

    public InstructionsLayout(final Context context, final AttributeSet attrs)
    {
        super(context, attrs);
    }

    public InstructionsLayout(final Context context, final AttributeSet attrs, final int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
    }

    public void init(@Nullable final Instructions instructions)
    {
        if (instructions == null)
        {
            return;
        }

        LayoutInflater.from(getContext()).inflate(R.layout.layout_instructions, this);
        ButterKnife.bind(this);

        if (instructions.getTitle() != null)
        {
            mTitle.setVisibility(VISIBLE);
            mTitle.setText(instructions.getTitle());
        }

        if (instructions.getChecklist() != null)
        {
            mCheckListsLayout.setVisibility(VISIBLE);
            for (ChecklistItem checklistItem : instructions.getChecklist())
            {
                final PreferenceView preferenceView = new PreferenceView(getContext());
                preferenceView.init(checklistItem);
                mCheckListsLayout.addView(preferenceView);
            }
        }
    }
}
