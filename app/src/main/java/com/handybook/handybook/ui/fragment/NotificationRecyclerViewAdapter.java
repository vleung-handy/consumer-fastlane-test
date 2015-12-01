package com.handybook.handybook.ui.fragment;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.handybook.handybook.R;
import com.handybook.handybook.module.notifications.model.HandyNotificationViewModel;
import com.squareup.picasso.Picasso;

import butterknife.Bind;
import butterknife.ButterKnife;

public class NotificationRecyclerViewAdapter extends RecyclerView.Adapter<NotificationRecyclerViewAdapter.ViewHolder>
{

    private HandyNotificationViewModel.List mNotifications;

    public NotificationRecyclerViewAdapter(final HandyNotificationViewModel.List notifications)
    {
        mNotifications = notifications;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_card_notification_default, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position)
    {
        final HandyNotificationViewModel notificationViewModel = mNotifications.get(position);
        holder.mItem = notificationViewModel;
        holder.title.setText(notificationViewModel.getTitle());
        holder.body.setText(notificationViewModel.getBody());
        holder.timestamp.setText(notificationViewModel.getTimestamp());
        Picasso.with(holder.mView.getContext())
                .load(notificationViewModel.getIconUrl(holder.mView.getContext()))
                .into(holder.icon);
    }

    @Override
    public int getItemCount()
    {
        return mNotifications.size();
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
        public ImageView icon;
        @Bind(R.id.notification_card_link_container)
        public LinearLayout linkContainer;
        @Bind(R.id.notification_card_button_container)
        public LinearLayout buttonContainer;
        @Bind(R.id.notification_card_timestamp)
        public TextView timestamp;

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
