package com.handybook.handybook.module.notifications.feed;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.handybook.handybook.module.notifications.feed.viewmodel.HandyNotificationViewModel;

public abstract class BaseNotificationViewHolder extends RecyclerView.ViewHolder
{

    public BaseNotificationViewHolder(final View itemView)
    {
        super(itemView);
    }

    abstract void bind(
            @NonNull final HandyNotificationViewModel handyNotificationViewModel,
            final int position
    );
}
