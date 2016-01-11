package com.handybook.handybook.ui.fragment;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.handybook.handybook.R;
import com.handybook.handybook.module.notifications.feed.model.HandyNotification;
import com.handybook.handybook.module.notifications.feed.viewmodel.HandyNotificationViewModel;
import com.squareup.picasso.Picasso;

import butterknife.Bind;
import butterknife.ButterKnife;

public class NotificationRecyclerViewAdapter extends RecyclerView.Adapter<NotificationRecyclerViewAdapter.ViewHolder>
{

    private HandyNotificationViewModel.List mHandyNotificationViewModels;

    public NotificationRecyclerViewAdapter(final HandyNotification.List notifications)
    {
        setHasStableIds(true);
        mHandyNotificationViewModels = HandyNotificationViewModel.List.from(notifications);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(parent.getContext()).inflate(viewType, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position)
    {
        final HandyNotificationViewModel notificationViewModel = getItem(position);
        holder.mItem = notificationViewModel;
        // Title
        holder.title.setText(notificationViewModel.getTitle());
        // Body
        holder.body.setText(Html.fromHtml(notificationViewModel.getHtmlBody()));
        // Timestamp
        holder.timestamp.setText(notificationViewModel.getTimestamp());
        // Icon
        Picasso.with(holder.mView.getContext())
                .load(notificationViewModel.getIconUrl(holder.mView.getContext()))
                .into(holder.image);
        // Action : Default
        if (notificationViewModel.hasDefaultAction())
        {
            holder.mView.setClickable(true);
            holder.mView.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(final View v)
                {
                    Intent intent = new Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse(notificationViewModel.getDefaultAction().getDeeplink())
                    );
                    v.getContext().startActivity(intent);
                }
            });

        }
        // Actions : Buttons
        holder.buttonContainer.setVisibility(
                notificationViewModel.hasButtonActions() ? View.VISIBLE : View.GONE
        );
        holder.buttonContainer.removeAllViews();
        for (final HandyNotification.Action action : notificationViewModel.getButtonActions())
        {
            Button button = new Button(holder.mView.getContext());
            holder.buttonContainer.addView(button);
            button.setLayoutParams(holder.templateCtaButton.getLayoutParams());
            button.setText(action.getText());
            // TODO: Implement onClick behaviour
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
        holder.linkContainer.setVisibility(
                notificationViewModel.hasLinkActions() ? View.VISIBLE : View.GONE
        );
        holder.linkContainer.removeAllViews();
        for (final HandyNotification.Action action : notificationViewModel.getLinkActions())
        {
            TextView textview = new TextView(holder.mView.getContext());
            holder.linkContainer.addView(textview);
            textview.setLayoutParams(holder.templateCta.getLayoutParams());
            textview.setText(action.getText());
            // TODO: Implement onClick behaviour
            textview.setOnClickListener(new View.OnClickListener()
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
        holder.divider.setVisibility(position == 0 ? View.GONE : View.VISIBLE);

    }

    private HandyNotificationViewModel getItem(final int position)
    {
        return mHandyNotificationViewModels.get(position);
    }

    @Override
    public int getItemCount()
    {
        if (mHandyNotificationViewModels == null)
        {
            return 0;
        }
        return mHandyNotificationViewModels.size();
    }

    @Override
    public int getItemViewType(final int position)
    {
        switch (getItem(position).getType())
        {
            case NOTIFICATION:
                return R.layout.layout_handy_notification_reminder;
            case PROMO:
                return R.layout.layout_handy_notification_promo;
            default:
                return R.layout.layout_handy_notification_reminder;
        }
    }

    public void mergeNotifications(final HandyNotification.List notifications)
    {
        //TODO: Actually merge them, not just swap
        mHandyNotificationViewModels = HandyNotificationViewModel.List.from(notifications);
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder
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
        @Bind(R.id.notification_card_cta_button_template)
        public Button templateCtaButton;
        @Bind(R.id.notification_card_cta_template)
        public TextView templateCta;
        @Bind(R.id.notification_card_divider)
        public FrameLayout divider;

        public ViewHolder(View view)
        {
            super(view);
            mView = view;
            ButterKnife.bind(this, mView);
        }

        @Override
        public String toString()
        {
            return super.toString() + " '" + title.getText() + "'";
        }
    }
}
