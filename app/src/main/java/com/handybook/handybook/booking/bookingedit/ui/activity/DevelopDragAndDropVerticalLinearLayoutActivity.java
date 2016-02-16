package com.handybook.handybook.booking.bookingedit.ui.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
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
    public static final String MOCK_INSTRUCTION_FILENAME = "mock_instructions.json";
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
        String jsonInstructions = StringUtils.readAssetFile(this, MOCK_INSTRUCTION_FILENAME);
        mInstructions = new Gson().fromJson(jsonInstructions, Instructions.class);
        mDndLayout.removeAllViews();
        mDndLayout.linkScrollView(mScrollView);
        for (ChecklistItem eCheckListItem : mInstructions.getChecklist())
        {
            BookingInstructionView biv = new BookingInstructionView(this);
            biv.reflect(eCheckListItem);
            mDndLayout.addView(biv);
        }
        mDndLayout.setOnChildMovedListener(new DragAndDropVerticalLinearLayout.OnChildMovedListener()
        {

            @Override
            public void onChildMoved(final View child, final int fromPosition, final int toPosition)
            {
                mTextOut.setText("Child moved:" + child + ", from: " + fromPosition + ", to: " + toPosition);
            }
        });
        mDndLayout.seOnChildrenSwappedListener(new DragAndDropVerticalLinearLayout.OnChildrenSwappedListener()
        {
            @Override
            public void onChildrenSwapped(final View childA, final int positionA, final View childB, final int positionB)
            {
                mTextOut.setText("Children swapped  [ ChildA: " + childA + ", posA: "
                        + positionA+ "] and [ ChildB: " + childB+ ", posB: "
                        + positionB + "]");
            }
        });
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
