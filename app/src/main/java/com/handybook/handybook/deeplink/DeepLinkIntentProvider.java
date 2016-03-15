package com.handybook.handybook.deeplink;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;

import com.airbnb.deeplinkdispatch.DeepLink;
import com.handybook.handybook.booking.ui.activity.BookingDetailActivity;
import com.handybook.handybook.booking.ui.activity.BookingsActivity;
import com.handybook.handybook.booking.ui.activity.PromosActivity;
import com.handybook.handybook.booking.ui.activity.ServiceCategoriesActivity;
import com.handybook.handybook.constant.BundleKeys;
import com.handybook.handybook.core.UserManager;
import com.handybook.handybook.helpcenter.ui.activity.HelpActivity;
import com.handybook.handybook.ui.activity.LoginActivity;
import com.handybook.handybook.ui.activity.ProfileActivity;

import javax.inject.Inject;

//TODO: package this better
public class DeepLinkIntentProvider
{
    //TODO: clean this up
    //TODO: can split this out so that each module has a routes file
    //TODO: put in properties?
    private static final String DEEP_LINK_BASE_URL = "handybook://deep_link/";
    private static final String DEEP_LINK_NEW_BASE_URL = "handy://";
    private static final String DEEP_LINK_SIDE_MENU_URL = DEEP_LINK_BASE_URL + "side_menu/";
    private static UserManager mUserManager;
    //TODO: REALLY don't want to make this static. can the filter be on the activity instead?
    //the deep link library requires that the annotated methods below be static

    @Inject
    public DeepLinkIntentProvider(@NonNull UserManager userManager)
    {
        mUserManager = userManager;
    }

    //TODO: move somewhere else
    //TODO: would rather not make user manager static, but annotated methods below are required to be by library
    public static boolean isUserLoggedIn()
    {
        return mUserManager != null && mUserManager.isUserLoggedIn();
    }

    @DeepLink({
            DEEP_LINK_BASE_URL + "home",
            DEEP_LINK_BASE_URL + "normal_flow",
            DEEP_LINK_BASE_URL + "promo_applied"})
    public static Intent getHomeIntent(Context context)
    {
        Intent intent = new Intent(context, ServiceCategoriesActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK
                | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        return intent;
    }

    public static Intent getLoginIntent(Context context)
    {
        Intent intent = new Intent(context, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK
                | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        return intent;
    }

    @DeepLink(DEEP_LINK_SIDE_MENU_URL + "promo")
    public static Intent getPromoIntent(Context context)
    {
        return new Intent(context, PromosActivity.class);
    }

    @DeepLink({DEEP_LINK_SIDE_MENU_URL + "mybookings",
            DEEP_LINK_NEW_BASE_URL + "bookings"})
    public static Intent getMyBookingsIntent(Context context)
    {
        if(isUserLoggedIn())
        {
            return new Intent(context, BookingsActivity.class);
        }
        return getLoginIntent(context);
    }

    @DeepLink({DEEP_LINK_SIDE_MENU_URL + "mybookings/id/{" + BundleKeys.DEEPLINK_BOOKING_ID + "}",
            DEEP_LINK_NEW_BASE_URL + "booking/id/{" + BundleKeys.DEEPLINK_BOOKING_ID + "}"})
    public static Intent getMyBookingDetailsIntent(Context context)
    {
        if (isUserLoggedIn())
        {
            return new Intent(context, BookingDetailActivity.class);
        }
        return getLoginIntent(context);
    }


    @DeepLink(DEEP_LINK_SIDE_MENU_URL + "account")
    public static Intent getAccountIntent(Context context)
    {
        if(isUserLoggedIn())
        {
            return new Intent(context, ProfileActivity.class);
        }
        return getHomeIntent(context);
    }

    @DeepLink(DEEP_LINK_SIDE_MENU_URL + "help")
    public static Intent getHelpIntent(Context context)
    {
        return new Intent(context, HelpActivity.class);
    }
}
