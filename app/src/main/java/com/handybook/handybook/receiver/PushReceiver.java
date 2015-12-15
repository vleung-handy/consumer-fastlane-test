package com.handybook.handybook.receiver;

import android.content.Context;

import com.handybook.handybook.util.NotificationActionUtils;
import com.urbanairship.push.BaseIntentReceiver;
import com.urbanairship.push.PushMessage;

public class PushReceiver extends BaseIntentReceiver
{
    @Override
    protected void onChannelRegistrationSucceeded(final Context context, final String s)
    {

    }

    @Override
    protected void onChannelRegistrationFailed(final Context context)
    {

    }

    @Override
    protected void onPushReceived(final Context context, final PushMessage pushMessage, final int i)
    {

    }

    @Override
    protected void onBackgroundPushReceived(final Context context, final PushMessage pushMessage)
    {

    }

    @Override
    protected boolean onNotificationOpened(final Context context, final PushMessage pushMessage,
                                           final int i)
    {
        return false;
    }

    @Override
    protected boolean onNotificationActionOpened(final Context context,
                                                 final PushMessage pushMessage,
                                                 final int i,
                                                 final String action,
                                                 final boolean b)
    {
        return NotificationActionUtils.handleAction(context, action, pushMessage.getPushBundle());
    }

}
