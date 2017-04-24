package com.handybook.handybook.push.action;

import com.handybook.handybook.R;
import com.urbanairship.push.notifications.NotificationActionButton;
import com.urbanairship.push.notifications.NotificationActionButtonGroup;

public class PushActionWidgets {

    public static NotificationActionButtonGroup createContactActionButtonGroup() {
        NotificationActionButton contactCallButton =
                new NotificationActionButton.Builder(PushActionConstants.ACTION_CONTACT_CALL)
                        .setLabel(R.string.call)
                        .setIcon(R.drawable.ic_phone)
                        .build();

        NotificationActionButton contactTextButton =
                new NotificationActionButton.Builder(PushActionConstants.ACTION_CONTACT_TEXT)
                        .setLabel(R.string.message)
                        .setIcon(R.drawable.ic_sms)
                        .build();

        return new NotificationActionButtonGroup.Builder()
                .addNotificationActionButton(contactCallButton)
                .addNotificationActionButton(contactTextButton)
                .build();
    }
}
