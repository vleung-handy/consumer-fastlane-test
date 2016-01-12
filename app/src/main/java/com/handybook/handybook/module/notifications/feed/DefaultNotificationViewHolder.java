package com.handybook.handybook.module.notifications.feed;

import android.content.Intent;
import android.net.Uri;
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
    public HandyNotificationViewModel mItem;
    public final View mView;

    @Bind(R.id.notification_card_title)
    public TextView title;
    @Bind(R.id.notification_card_body)
    public TextView body;
    @Bind(R.id.notification_card_icon)
    public ImageView image;
    @Bind(R.id.notification_card_link_container)
    public LinearLayout linkContainer;
    @Bind(R.id.notification_card_button_container)
    public LinearLayout buttonContainer;
    @Bind(R.id.notification_card_timestamp)
    public TextView timestamp;
    @Bind(R.id.notification_card_divider)
    public FrameLayout divider;

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
        title.setText(mItem.getTitle());
        // Body
        body.setText(Html.fromHtml(mItem.getHtmlBody()));
        // Timestamp
        timestamp.setText(mItem.getTimestamp());
        // Icon
        Picasso.with(mView.getContext())
                .load(mItem.getIconUrl(mView.getContext()))
                .into(image);
        // Action : Default
        if (mItem.hasDefaultAction())
        {
            mView.setClickable(true);
            mView.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(final View v)
                {
                    Intent intent = new Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse(mItem.getDefaultAction().getDeeplink())
                    );
                    v.getContext().startActivity(intent);
                }
            });
        }
        // Actions : Buttons
        buttonContainer.setVisibility(
                mItem.hasButtonActions() ? View.VISIBLE : View.GONE
        );
        buttonContainer.removeAllViews();
        for (final HandyNotification.Action action : mItem.getButtonActions())
        {
            Button button = (Button) View.inflate(
                    mView.getContext(),
                    R.layout.layout_handy_notification_cta_button,
                    null
            );
            buttonContainer.addView(button);
            button.setText(action.getText());
            button.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(final View v)
                {
                    Intent intent = new Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse(action.getDeeplink())
                    );
                    v.getContext().startActivity(intent);
                }
            });
        }
        // Actions : Links
        linkContainer.setVisibility(
                mItem.hasLinkActions() ? View.VISIBLE : View.GONE
        );
        linkContainer.removeAllViews();
        for (final HandyNotification.Action action : mItem.getLinkActions())
        {
            TextView textView = (TextView) View.inflate(
                    mView.getContext(),
                    R.layout.layout_handy_notification_cta_link,
                    null
            );
            linkContainer.addView(textView);
            textView.setText(action.getText());
            textView.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(final View v)
                {
                    Intent intent = new Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse(action.getDeeplink())
                    );
                    v.getContext().startActivity(intent);
                }
            });
        }
        // Divider
        divider.setVisibility(position == 0 ? View.GONE : View.VISIBLE);
    }

    @Override
    public String toString()
    {
        return super.toString() + " '" + title.getText() + "'";
    }
}
