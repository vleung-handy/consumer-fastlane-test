package com.handybook.handybook.core.ui.fragment;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.handybook.handybook.R;
import com.handybook.handybook.notifications.viewmodel.HandyNotificationViewModel;
import com.handybook.handybook.notifications.model.HandyNotification;
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
        holder.title.setText(notificationViewModel.getTitle());
        holder.body.setText(notificationViewModel.getBody());
        holder.timestamp.setText(notificationViewModel.getTimestamp());
        Picasso.with(holder.mView.getContext())
                .load(notificationViewModel.getIconUrl(holder.mView.getContext()))
                .into(holder.image);
    }

    private HandyNotificationViewModel getItem(final int position)
    {
        return mHandyNotificationViewModels.get(position);
    }

    @Override
    public int getItemCount()
    {
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
