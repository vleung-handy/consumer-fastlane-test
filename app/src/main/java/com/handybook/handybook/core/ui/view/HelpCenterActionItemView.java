package com.handybook.handybook.core.ui.view;

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
import com.handybook.handybook.logger.handylogger.LogEvent;
import com.handybook.handybook.logger.handylogger.model.HelpCenterLog;

import org.greenrobot.eventbus.EventBus;

import butterknife.BindView;
import butterknife.ButterKnife;

public class HelpCenterActionItemView extends FrameLayout implements View.OnClickListener {

    @BindView(R.id.help_center_action_layout)
    ViewGroup mHelpCenterActionLayout;
    @BindView(R.id.help_action_image)
    ImageView mHelpActionImage;
    @BindView(R.id.help_action_title)
    TextView mHelpActionTitle;
    @BindView(R.id.help_action_subtitle)
    TextView mHelpActionSubtitle;

    private String mDeepLink;
    private String mTitle;
    private EventBus mBus;

    public HelpCenterActionItemView(final Context context, final EventBus bus) {
        super(context);
        init(bus);
    }

    public HelpCenterActionItemView(
            final Context context, final AttributeSet attrs,
            final EventBus bus
    ) {
        super(context, attrs);
        init(bus);
    }

    public HelpCenterActionItemView(
            final Context context,
            final AttributeSet attrs,
            final int defStyleAttr,
            final EventBus bus
    ) {
        super(context, attrs, defStyleAttr);
        init(mBus);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public HelpCenterActionItemView(
            final Context context,
            final AttributeSet attrs,
            final int defStyleAttr,
            final int defStyleRes,
            final EventBus bus
    ) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(bus);
    }

    public void setDisplay(String title, String subtitle, String deeplink, @DrawableRes int icon) {
        mTitle = title;
        mDeepLink = deeplink;
        mHelpActionTitle.setText(title);
        mHelpActionSubtitle.setText(subtitle);
        mHelpActionImage.setImageResource(icon);
    }

    private void init(final EventBus bus) {
        mBus = bus;
        inflate(getContext(), R.layout.element_help_center_action, this);
        ButterKnife.bind(this);
        mHelpCenterActionLayout.setOnClickListener(this);
    }

    @Override
    public void onClick(final View v) {
        if (!Strings.isNullOrEmpty(mDeepLink)) {
            if (!Strings.isNullOrEmpty(mTitle)) {
                mBus.post(new LogEvent.AddLogEvent(new HelpCenterLog.HelpLinkTappedLog(
                        mTitle, mDeepLink
                )));
            }
            final Intent intent = new Intent(getContext(), DeepLinkActivity.class);
            intent.setData(Uri.parse(mDeepLink));
            getContext().startActivity(intent);
        }
    }
}
