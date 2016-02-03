package com.handybook.handybook.booking.bookingedit.ui.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.handybook.handybook.R;
import com.handybook.handybook.booking.model.ChecklistItem;
import com.handybook.handybook.booking.model.Instructions;
import com.handybook.handybook.booking.ui.widget.BookingInstructionView;
import com.handybook.handybook.booking.ui.widget.DragAndDropVerticalLinearLayout;
import com.handybook.handybook.util.StringUtils;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class DevelopDragAndDropVerticalLinearLayoutActivity extends AppCompatActivity
{
    @Bind(R.id.develop_scrollview)
    ScrollView mScrollView;
    @Bind(R.id.develop_dnd)
    DragAndDropVerticalLinearLayout mDndLayout;
    @Bind(R.id.develop_text_out)
    TextView mTextOut;

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

    @OnClick(R.id.develop_swap_one)
    void swap1()
    {
        mDndLayout.swapChildren(1, 2);
    }

    @OnClick(R.id.develop_swap_two)
    void swap2()
    {
        mDndLayout.swapChildren(2, 1);
    }

    @OnClick(R.id.develop_swap_three)
    void swap3()
    {
        mDndLayout.swapChildren(0, 3);
    }

    @OnClick(R.id.develop_swap_four)
    void swap4()
    {
        mDndLayout.swapChildren(3, 0);
    }

    @OnClick(R.id.develop_move_one)
    void move1()
    {
        mDndLayout.moveChild(0, 3);
    }

    @OnClick(R.id.develop_move_two)
    void move2()
    {
        mDndLayout.moveChild(3, 0);
    }


}
