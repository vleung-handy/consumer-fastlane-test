package com.handybook.handybook.booking.bookingedit.ui.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ScrollView;

import com.google.gson.Gson;
import com.handybook.handybook.R;
import com.handybook.handybook.booking.model.ChecklistItem;
import com.handybook.handybook.booking.model.Instructions;
import com.handybook.handybook.booking.ui.widget.BookingInstructionView;
import com.handybook.handybook.booking.ui.widget.DragAndDropVerticalLinearLayout;
import com.handybook.handybook.util.StringUtils;

import butterknife.Bind;
import butterknife.ButterKnife;

public class DevelopDragAndDropVerticalLinearLayoutActivity extends AppCompatActivity
{
    @Bind(R.id.develop_scrollview)
    ScrollView mScrollView;
    @Bind(R.id.develop_dnd)
    DragAndDropVerticalLinearLayout mDndLayout;

    private Instructions mInstructions;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout._develop_drag_and_drop_vertical_linear_layout);
        ButterKnife.bind(this);
        String jsonInstructions = StringUtils.readAssetFile(this, "mock_instructions.json");
        mInstructions = new Gson().fromJson(jsonInstructions, Instructions.class);
        mDndLayout.removeAllViews();
        mDndLayout.linkScrollView(mScrollView);
        for (ChecklistItem eCheckListItem : mInstructions.getChecklist())
        {
            BookingInstructionView biv = new BookingInstructionView(this);
            biv.reflect(eCheckListItem);
            mDndLayout.addView(biv);
        }
    }
}
