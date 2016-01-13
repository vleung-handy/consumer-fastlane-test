package com.handybook.handybook.module.notifications.feed;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.handybook.handybook.R;
import com.handybook.handybook.module.notifications.feed.model.HandyNotification;
import com.handybook.handybook.module.notifications.feed.viewmodel.HandyNotificationViewModel;
import com.handybook.handybook.ui.transformation.RoundedTransformation;
import com.squareup.picasso.Picasso;

import butterknife.Bind;
import butterknife.ButterKnife;

public class PromoNotificationViewHolder extends BaseNotificationViewHolder
{
    public HandyNotificationViewModel mItem;
    public final View mView;

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

    private PromoNotificationViewHolder(View view)
    {
        super(view);
        mView = view;
        ButterKnife.bind(this, mView);
    }

    public static PromoNotificationViewHolder newInstance(@NonNull final ViewGroup parentView)
    {
        return new PromoNotificationViewHolder(
                LayoutInflater.from(parentView.getContext()).inflate(
                        R.layout.layout_handy_notification_promo,
                        parentView,
                        false
                )
        );
    }

    public void bind(@NonNull final HandyNotificationViewModel model, final int position)
    {
        mItem = model;
        // Body
        body.setText(Html.fromHtml(mItem.getHtmlBody()));
        // Timestamp
        timestamp.setText(mItem.getTimestamp());
        // Image
        image.getViewTreeObserver()
                .addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener()
                {
                    @Override
                    public void onGlobalLayout()
                    {
                        image.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                        Picasso.with(mView.getContext())
                                .load(mItem.getIconUrl(mView.getContext()))
                                .resize(image.getWidth(), 0)
                                .transform(
                                        new RoundedTransformation(
                                                5.0f,
                                                0f,
                                                true,
                                                false,
                                                false,
                                                true
                                        )
                                ).into(image);
                    }
                });
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
            Button button = (Button) LayoutInflater.from(mView.getContext()).inflate(
                    R.layout.layout_handy_notification_cta_button,
                    buttonContainer,
                    false
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
            TextView textView = (TextView) LayoutInflater.from(mView.getContext()).inflate(
                    R.layout.layout_handy_notification_cta_link,
                    linkContainer,
                    false
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
}
