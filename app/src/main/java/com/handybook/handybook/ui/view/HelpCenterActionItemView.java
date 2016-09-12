package com.handybook.handybook.ui.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.DrawableRes;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.common.base.Strings;
import com.handybook.handybook.R;
import com.handybook.handybook.deeplink.DeepLinkActivity;

import butterknife.Bind;
import butterknife.ButterKnife;


public class HelpCenterActionItemView extends FrameLayout implements View.OnClickListener
{
    @Bind(R.id.help_center_action_layout)
    ViewGroup mHelpCenterActionLayout;
    @Bind(R.id.help_action_image)
    ImageView mHelpActionImage;
    @Bind(R.id.help_action_title)
    TextView mHelpActionTitle;
    @Bind(R.id.help_action_subtitle)
    TextView mHelpActionSubtitle;

    private String mDeepLink;

    public HelpCenterActionItemView(final Context context)
    {
        super(context);
        init();
    }

    public HelpCenterActionItemView(final Context context, final AttributeSet attrs)
    {
        super(context, attrs);
        init();
    }

    public HelpCenterActionItemView(final Context context, final AttributeSet attrs, final int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
        init();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public HelpCenterActionItemView(final Context context, final AttributeSet attrs, final int defStyleAttr, final int defStyleRes)
    {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    public void setDisplay(String title, String subtitle, String deeplink, @DrawableRes int icon)
    {
        mHelpActionTitle.setText(title);
        mHelpActionSubtitle.setText(subtitle);
        mDeepLink = deeplink;
        mHelpActionImage.setImageResource(icon);
    }

    private void init()
    {
        inflate(getContext(), R.layout.element_help_center_action, this);
        ButterKnife.bind(this);
        mHelpCenterActionLayout.setOnClickListener(this);
    }

    @Override
    public void onClick(final View v)
    {
        if (!Strings.isNullOrEmpty(mDeepLink))
        {
            final Intent intent = new Intent(getContext(), DeepLinkActivity.class);
            intent.setData(Uri.parse(mDeepLink));
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK |
                    Intent.FLAG_ACTIVITY_CLEAR_TOP);
            getContext().startActivity(intent);
        }
    }
}
