package com.handybook.handybook.manager;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;

import com.airbnb.deeplinkdispatch.DeepLink;
import com.handybook.handybook.booking.ui.activity.BookingsActivity;
import com.handybook.handybook.booking.ui.activity.ServiceCategoriesActivity;
import com.handybook.handybook.booking.ui.fragment.PromosActivity;
import com.handybook.handybook.core.UserManager;
import com.handybook.handybook.helpcenter.ui.activity.HelpActivity;
import com.handybook.handybook.ui.activity.ProfileActivity;

import javax.inject.Inject;

public class DeepLinkNavigationManager
{
    //TODO: can split this out so that each module has a routes file
    //TODO: investigate possible params for each link
    //TODO: put in properties?
    private static final String DEEP_LINK_BASE_URL = "handybook://deep_link/";
    private static final String DEEP_LINK_SIDE_MENU_URL = DEEP_LINK_BASE_URL + "side_menu/";
    private static UserManager mUserManager;
    //TODO: REALLY don't want to make this static. can the filter be on the activity instead?
    //the deep link library requires that the annotated methods below be static

    @Inject
    public DeepLinkNavigationManager(@NonNull UserManager userManager)
    {
        mUserManager = userManager;
    }

    //TODO: move somewhere else
    //would rather not make user manager static, but annotated methods below are required to be
    public static boolean isUserLoggedIn()
    {
        //TODO: can these methods be invoked before the constructor?
        return mUserManager != null && mUserManager.isUserLoggedIn();
    }

    @DeepLink(DEEP_LINK_BASE_URL + "home")
    public static Intent getHomeIntent(Context context)
    {
        return new Intent(context, ServiceCategoriesActivity.class);
    }

    @DeepLink(DEEP_LINK_SIDE_MENU_URL + "help")
    public static Intent getHelpIntent(Context context)
    {
        return new Intent(context, HelpActivity.class);
    }

    @DeepLink(DEEP_LINK_SIDE_MENU_URL + "mybookings")
    public static Intent getMyBookingsIntent(Context context)
    {
        if(isUserLoggedIn())
        {
            return new Intent(context, BookingsActivity.class);
        }
        return getHomeIntent(context);
    }

    @DeepLink(DEEP_LINK_SIDE_MENU_URL + "promo")
    public static Intent getPromoIntent(Context context)
    {
        return new Intent(context, PromosActivity.class);
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
}
