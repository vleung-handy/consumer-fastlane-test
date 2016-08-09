package com.handybook.handybook.booking.ui.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.handybook.handybook.R;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ProMilestoneView extends FrameLayout
{
    @Bind(R.id.pro_milestone_dot)
    View mDot;
    @Bind(R.id.pro_milestone_line)
    View mLine;
    @Bind(R.id.pro_milestone_title)
    TextView mTitleText;
    @Bind(R.id.pro_milestone_body)
    TextView mBodyText;
    @Bind(R.id.pro_milestone_call)
    Button mCallButton;
    @Bind(R.id.pro_milestone_text)
    Button mTextButton;

    public ProMilestoneView(final Context context)
    {
        super(context);
        init();
    }

    public ProMilestoneView(final Context context, final AttributeSet attrs)
    {
        super(context, attrs);
        init();
    }

    public ProMilestoneView(final Context context, final AttributeSet attrs, final int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
        init();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public ProMilestoneView(final Context context, final AttributeSet attrs, final int defStyleAttr, final int defStyleRes)
    {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init()
    {
        inflate(getContext(), R.layout.view_pro_milestone, this);
        ButterKnife.bind(this);
    }

    public void setLineVisibility(int visibility) { mLine.setVisibility(visibility); }

    public void setDotColor(int drawableId) { mDot.setBackgroundResource(drawableId); }

    public void setTitleText(CharSequence text) { mTitleText.setText(text); }

    public void setBodyText(CharSequence text) { mBodyText.setText(text); }

    public void setCallAndTextButtonVisibility(int visibility)
    {
        mCallButton.setVisibility(visibility);
        mTextButton.setVisibility(visibility);
    }

    public void setCallButtonOnClickListener(OnClickListener onClickListener)
    {
        mCallButton.setOnClickListener(onClickListener);
    }

    public void setTextButtonOnClickListener(OnClickListener onClickListener)
    {
        mTextButton.setOnClickListener(onClickListener);
    }
}
