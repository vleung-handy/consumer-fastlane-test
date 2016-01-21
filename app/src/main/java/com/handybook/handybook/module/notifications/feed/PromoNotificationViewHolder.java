package com.handybook.handybook.module.notifications.feed;

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
    final View mView;
    HandyNotificationViewModel mItem;

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

    HandyNotificationActionHandler mNotificationActionHandler;

    private PromoNotificationViewHolder(View view,
                                        @NonNull HandyNotificationActionHandler notificationActionHandler)
    {
        super(view);
        mView = view;
        ButterKnife.bind(this, mView);
        mNotificationActionHandler = notificationActionHandler;
    }

    public static PromoNotificationViewHolder newInstance(@NonNull final ViewGroup parentView,
                                                          @NonNull HandyNotificationActionHandler notificationActionHandler)
    {
        return new PromoNotificationViewHolder(
                LayoutInflater.from(parentView.getContext()).inflate(
                        R.layout.layout_handy_notification_promo,
                        parentView,
                        false
                ), notificationActionHandler
        );

    }

    public void bind(@NonNull final HandyNotificationViewModel model, final int position)
    {
        mItem = model;
        // Body
        mBody.setText(Html.fromHtml(mItem.getHtmlBody()));
        // Timestamp
        mTimestamp.setText(mItem.getTimestamp());
        // Image
        mImage.getViewTreeObserver()
                .addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener()
                {
                    @Override
                    public void onGlobalLayout()
                    {
                        mImage.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                        Picasso.with(mView.getContext())
                                .load(mItem.getIconUrl(mView.getContext()))
                                .resize(mImage.getWidth(), 0)
                                .transform(
                                        new RoundedTransformation(
                                                5.0f,
                                                0f,
                                                true,
                                                false,
                                                false,
                                                true
                                        )
                                ).into(mImage);
                    }
                });
        // Actions : "call_to_action"
        if (mItem.hasLinkActions())
        {
            mView.setClickable(true);
            mView.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(final View v)
                {
                    mNotificationActionHandler.handleNotificationPromoItemClicked(mItem);
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
                    mNotificationActionHandler.handleNotificationAction(action);
                }
            });
        }
        // Divider
        mDivider.setVisibility(position == 0 ? View.GONE : View.VISIBLE);
        // Read Indicator
        mReadIndicator.setVisibility(mItem.isUnread() ? View.VISIBLE : View.GONE);
    }
}
