package com.handybook.handybook.module.notifications.feed;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import com.handybook.handybook.module.notifications.feed.model.HandyNotification;
import com.handybook.handybook.module.notifications.feed.viewmodel.HandyNotificationViewModel;

public class NotificationRecyclerViewAdapter
        extends RecyclerView.Adapter<BaseNotificationViewHolder>
{

    private HandyNotificationViewModel.List mHandyNotificationViewModels;
    private HandyNotificationActionHandler mHandyNotificationActionHandler;

    public NotificationRecyclerViewAdapter(final HandyNotification.List notifications,
                                           @NonNull final HandyNotificationActionHandler handyNotificationActionHandler
    )
    {
        setHasStableIds(true);
        mHandyNotificationViewModels = HandyNotificationViewModel.List.from(notifications);
        mHandyNotificationActionHandler = handyNotificationActionHandler;
    }

    @Override
    public BaseNotificationViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        switch (HandyNotification.HandyNotificationType.values()[viewType])
        {
            case NOTIFICATION:
                return DefaultNotificationViewHolder.newInstance(parent, mHandyNotificationActionHandler);
            case PROMO:
                return PromoNotificationViewHolder.newInstance(parent, mHandyNotificationActionHandler);
            default:
                return DefaultNotificationViewHolder.newInstance(parent, mHandyNotificationActionHandler);
        }
    }

    @Override
    public void onBindViewHolder(final BaseNotificationViewHolder holder, int position)
    {
        final HandyNotificationViewModel notificationViewModel = getItem(position);
        holder.bind(notificationViewModel, position);
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
        return getItem(position).getType().ordinal();
    }

    public void mergeNotifications(final HandyNotification.List notifications)
    {
        //TODO: Actually merge them, not just swap
        mHandyNotificationViewModels = HandyNotificationViewModel.List.from(notifications);
        notifyDataSetChanged();
    }

}
