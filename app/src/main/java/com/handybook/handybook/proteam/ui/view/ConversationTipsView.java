package com.handybook.handybook.proteam.ui.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.support.annotation.StringRes;
import android.util.AttributeSet;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.handybook.handybook.R;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ConversationTipsView extends FrameLayout
{
    @Bind(R.id.conversation_tips_title_text)
    TextView mTitleText;
    @Bind(R.id.conversation_tips_body_text)
    TextView mBodyText;

    public ConversationTipsView(final Context context)
    {
        super(context);
        init();
    }

    public ConversationTipsView(final Context context, final AttributeSet attrs)
    {
        super(context, attrs);
        init();
    }

    public ConversationTipsView(
            final Context context,
            final AttributeSet attrs,
            final int defStyleAttr
    )
    {
        super(context, attrs, defStyleAttr);
        init();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public ConversationTipsView(
            final Context context,
            final AttributeSet attrs,
            final int defStyleAttr,
            final int defStyleRes
    )
    {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init()
    {
        inflate(getContext(), R.layout.view_conversation_tips, this);
        ButterKnife.bind(this);
    }

    public void setTitle(@StringRes int resId)
    {
        mTitleText.setText(resId);
    }

    public void setBody(@StringRes int resId)
    {
        mBodyText.setText(resId);
    }

    @OnClick(R.id.conversation_tips_dismiss_btn)
    public void dismiss()
    {
        setVisibility(GONE);
    }
}
