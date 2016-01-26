package com.handybook.handybook.ui.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.handybook.handybook.R;

import butterknife.Bind;
import butterknife.ButterKnife;

public class LeftIconButton extends FrameLayout
{
    @Bind(R.id.label)
    TextView mLabel;
    @Bind(R.id.icon)
    ImageView mIcon;

    public LeftIconButton(final Context context)
    {
        super(context);
    }

    public LeftIconButton(final Context context, final AttributeSet attrs)
    {
        super(context, attrs);
    }

    public LeftIconButton(final Context context, final AttributeSet attrs, final int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public LeftIconButton(
            final Context context, final AttributeSet attrs,
            final int defStyleAttr, final int defStyleRes
    )
    {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public void init(final int textId, final int iconId, final int iconBackgroundId)
    {
        LayoutInflater.from(getContext()).inflate(R.layout.element_left_icon_button, this);
        ButterKnife.bind(this);
        mLabel.setText(textId);
        mIcon.setImageDrawable(getResources().getDrawable(iconId));
        mIcon.setBackgroundResource(iconBackgroundId);
    }
}
