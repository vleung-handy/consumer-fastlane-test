package com.handybook.handybook.module.notifications.feed;

import com.handybook.handybook.module.notifications.feed.model.HandyNotification;
import com.handybook.handybook.module.notifications.feed.viewmodel.HandyNotificationViewModel;

/**
 * currently implemented by the notification feed fragment to handle actions made
 * by the notification feed items. this way we can access the event bus and do things
 * that require an activity context
 */
public interface HandyNotificationActionHandler
{
    boolean handleNotificationAction(HandyNotification.Action action);
    void handleNotificationPromoItemClicked(HandyNotificationViewModel promoNotificationViewModel);

}
