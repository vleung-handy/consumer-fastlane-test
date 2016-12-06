package com.handybook.handybook.receiver;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.Nullable;

import com.handybook.handybook.deeplink.DeepLinkIntentProvider;
import com.handybook.handybook.module.proteam.ui.activity.ProMessagesActivity;
import com.handybook.shared.layer.LayerConstants;
import com.handybook.shared.layer.receiver.PushNotificationReceiver;
import com.layer.sdk.messaging.Message;

public class LayerPushReceiver extends PushNotificationReceiver
{
    private static final String MESSAGES_DEEPLINK =
            DeepLinkIntentProvider.DEEP_LINK_BASE_URL + "pro_team";

    @Override
    public void onReceive(final Context context, final Intent intent)
    {
        if (LayerConstants.ACTION_PUSH.equals(intent.getAction()))
        {
            final Intent orderedBroadcastIntent = new Intent(LayerConstants.ACTION_SHOW_NOTIFICATION);
            orderedBroadcastIntent.putExtras(intent.getExtras());
            context.sendOrderedBroadcast(orderedBroadcastIntent, null);
        }
        else
        {
            super.onReceive(context, intent);
        }
    }

    @Nullable
    @Override
    protected PendingIntent createNotificationClickIntent(
            final Context context,
            @Nullable final Message message
    )
    {
        Intent intent;
        if (message != null)
        {
            intent = new Intent(context, ProMessagesActivity.class)
                    .setPackage(context.getApplicationContext().getPackageName())
                    .putExtra(
                            LayerConstants.LAYER_CONVERSATION_KEY,
                            message.getConversation().getId()
                    )
                    .putExtra(LayerConstants.LAYER_MESSAGE_KEY, message.getId())
                    .putExtra(LayerConstants.KEY_BACK_NAVIGATION_DEEPLINK, MESSAGES_DEEPLINK)
                    .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        }
        else
        {
            intent = new Intent(Intent.ACTION_VIEW, Uri.parse(MESSAGES_DEEPLINK))
                    .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        }
        return PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_ONE_SHOT);
    }
}
