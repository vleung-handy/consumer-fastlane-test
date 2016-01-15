package com.handybook.handybook.module.notifications.feed;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.crashlytics.android.Crashlytics;
import com.handybook.handybook.deeplink.DeepLinkUtils;
import com.handybook.handybook.module.notifications.feed.model.HandyNotification;
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

    /**
     *
     * @param action
     * @param context
     * @return true only if action was successfully handled
     */
    protected boolean handleNotificationAction(@Nullable HandyNotification.Action action,
                                            @NonNull Context context)
    {
        if (action == null)
        {
            Crashlytics.logException(
                    new RuntimeException("Action is now null"));
        }
        else
        {
            final String deeplink = action.getDeeplink();
            if (deeplink == null)
            {
                Crashlytics.logException(new RuntimeException("Action without a deeplink received: " + action.toString()));
            }
            else
            {
                return DeepLinkUtils.safeLaunchDeepLink(deeplink, context);
            }
        }
        return false;
    }
}
