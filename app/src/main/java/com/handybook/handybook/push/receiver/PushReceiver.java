package com.handybook.handybook.push.receiver;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;

import com.handybook.handybook.core.constant.BundleKeys;
import com.handybook.handybook.core.BaseApplication;
import com.handybook.handybook.core.User;
import com.handybook.handybook.core.UserManager;
import com.handybook.handybook.push.action.PushActionHandler;
import com.handybook.handybook.push.deeplink.DeeplinkHandler;
import com.urbanairship.push.BaseIntentReceiver;
import com.urbanairship.push.PushMessage;

import javax.inject.Inject;

public class PushReceiver extends BaseIntentReceiver
{
    @Inject
    UserManager mUserManager;

    @Override
    public void onReceive(final Context context, final Intent intent)
    {
        ((BaseApplication) context.getApplicationContext()).inject(this);
        super.onReceive(context, intent);
    }

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
        final Bundle pushBundle = pushMessage.getPushBundle();
        return isValidUser(pushBundle) && DeeplinkHandler.handleDeeplink(context, pushBundle);
    }

    @Override
    protected boolean onNotificationActionOpened(final Context context,
                                                 final PushMessage pushMessage,
                                                 final int i,
                                                 final String action,
                                                 final boolean b)
    {
        final Bundle pushBundle = pushMessage.getPushBundle();
        return isValidUser(pushBundle) &&
                PushActionHandler.handleAction(context, action, pushMessage.getPushBundle());
    }

    private boolean isValidUser(@NonNull final Bundle pushBundle)
    {
        // make sure user id in push notification matches the logged-in user id
        final String pushUserId = pushBundle.getString(BundleKeys.USER_ID);
        final User currentUser = mUserManager.getCurrentUser();
        return pushUserId == null ||
                (currentUser != null && pushUserId.equals(currentUser.getId()));
    }
}
