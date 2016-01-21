package com.handybook.handybook.module.notifications;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.handybook.handybook.module.notifications.feed.model.HandyNotification;
import com.handybook.handybook.module.notifications.feed.viewmodel.HandyNotificationViewModel;
import com.handybook.handybook.module.notifications.splash.model.SplashPromo;
import com.handybook.handybook.util.ValidationUtils;

/**
 * needed because the model for promos in the notification feed is different from the splash
 */
public abstract class HandyNotificationSplashPromoConverter
{
    /**
     * currently used by the notification feed fragment
     *
     * needed to convert the view model in the promo notification view holder to a SplashPromo
     *
     * need to use ViewModel instead of the underlying model because we need its getIconUrl() function,
     * which retrieves the correct icon url based on the device screen density
     * @param handyNotificationViewModel the view model of the promo view holder that was clicked
     * @param context
     * @return
     */
    @Nullable
    public static SplashPromo convertToSplashPromo(
            @NonNull HandyNotificationViewModel handyNotificationViewModel,
            @NonNull Context context
    )
    //would rather get the model, but need the ViewModel because we need its getIconUrl() function
    {
        HandyNotification.Action linkActions[] = handyNotificationViewModel.getLinkActions();
        if (ValidationUtils.isNullOrEmpty(linkActions)
                || handyNotificationViewModel.getType() != HandyNotification.HandyNotificationType.PROMO)
        {
            return null;
        }
        String promoUniqueId = null; //the notification payload currently doesn't have the promo unique id
        String imageUrl = handyNotificationViewModel.getIconUrl(context);
        String title = handyNotificationViewModel.getTitle();
        String subtitle = handyNotificationViewModel.getBody();

        HandyNotification.Action splashPromoAction = linkActions[0];
        String deepLinkUrl = splashPromoAction.getDeeplink();
        String actionText = splashPromoAction.getText();

        return new SplashPromo(promoUniqueId, imageUrl, title, subtitle, deepLinkUrl, actionText);
    }
}
