package com.handybook.handybook.module.notifications.feed;

import android.support.annotation.NonNull;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.handybook.handybook.R;
import com.handybook.handybook.module.notifications.feed.model.HandyNotification;
import com.handybook.handybook.module.notifications.feed.viewmodel.HandyNotificationViewModel;
import com.squareup.picasso.Picasso;

import butterknife.Bind;
import butterknife.ButterKnife;

public class DefaultNotificationViewHolder extends BaseNotificationViewHolder
{
    HandyNotificationViewModel mItem;
    final View mView;

    @Bind(R.id.notification_card_title)
    TextView mTitle;
    @Bind(R.id.notification_card_body)
    TextView mBody;
    @Bind(R.id.notification_card_icon)
    ImageView mImage;
    @Bind(R.id.notification_card_link_container)
    LinearLayout mLinkContainer;
    @Bind(R.id.notification_card_button_container)
    LinearLayout mButtonContainer;
    @Bind(R.id.notification_card_timestamp)
    TextView mTimestamp;
    @Bind(R.id.notification_card_divider)
    FrameLayout mDivider;
    @Bind(R.id.notification_card_indicator_read)
    View mReadIndicator;

    private DefaultNotificationViewHolder(View view)
    {
        super(view);
        mView = view;
        ButterKnife.bind(this, mView);
    }

    public static DefaultNotificationViewHolder newInstance(@NonNull final ViewGroup parentView)
    {
        return new DefaultNotificationViewHolder(
                LayoutInflater.from(parentView.getContext()).inflate(
                        R.layout.layout_handy_notification_default,
                        parentView,
                        false
                )
        );
    }

    public void bind(@NonNull final HandyNotificationViewModel model, final int position)
    {
        mItem = model;
        // Title
        mTitle.setText(mItem.getTitle());
        // Body
        mBody.setText(Html.fromHtml(mItem.getHtmlBody()));
        // Timestamp
        mTimestamp.setText(mItem.getTimestamp());
        // Icon
        Picasso.with(mView.getContext())
                .load(mItem.getIconUrl(mView.getContext()))
                .placeholder(R.drawable.ic_noimage)
                .error(R.drawable.ic_noimage)
                .into(mImage);
        // Action : Default
        if (mItem.hasDefaultAction())
        {
            mView.setClickable(true);
            mView.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(final View v)
                {
                    final HandyNotification.Action action = mItem.getDefaultAction();
                    handleNotificationAction(action, v.getContext());
                }
            });
        }
        // Actions : Buttons
        mButtonContainer.setVisibility(
                mItem.hasButtonActions() ? View.VISIBLE : View.GONE
        );
        mButtonContainer.removeAllViews();
        for (final HandyNotification.Action action : mItem.getButtonActions())
        {
            Button button = (Button) LayoutInflater.from(mView.getContext()).inflate(
                    R.layout.layout_handy_notification_cta_button,
                    mButtonContainer,
                    false
            );
            mButtonContainer.addView(button);
            button.setText(action.getText());
            button.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(final View v)
                {
                    handleNotificationAction(action, v.getContext());
                }
            });
        }
        // Actions : Links
        mLinkContainer.setVisibility(
                mItem.hasLinkActions() ? View.VISIBLE : View.GONE
        );
        mLinkContainer.removeAllViews();
        for (final HandyNotification.Action action : mItem.getLinkActions())
        {
            TextView textView = (TextView) LayoutInflater.from(mView.getContext()).inflate(
                    R.layout.layout_handy_notification_cta_link,
                    mButtonContainer,
                    false
            );
            mLinkContainer.addView(textView);
            textView.setText(action.getText());
            textView.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(final View v)
                {
                    handleNotificationAction(action, v.getContext());
                }
            });
        }
        // Divider
        mDivider.setVisibility(position == 0 ? View.GONE : View.VISIBLE);
        // Read Indicator
        mReadIndicator.setVisibility(mItem.isUnread() ? View.VISIBLE : View.GONE);
    }

    @Override
    public String toString()
    {
        return super.toString() + " '" + mTitle.getText() + "'";
    }
}
