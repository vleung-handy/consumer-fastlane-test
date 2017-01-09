package com.handybook.handybook.deeplink;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.airbnb.deeplinkdispatch.DeepLink;
import com.handybook.handybook.account.ui.ProfileActivity;
import com.handybook.handybook.booking.history.HistoryActivity;
import com.handybook.handybook.booking.ui.activity.BookingDetailActivity;
import com.handybook.handybook.booking.ui.activity.BookingsActivity;
import com.handybook.handybook.booking.ui.activity.PromosActivity;
import com.handybook.handybook.bottomnav.BottomNavActivity;
import com.handybook.handybook.configuration.manager.ConfigurationManager;
import com.handybook.handybook.core.MainNavTab;
import com.handybook.handybook.core.UserManager;
import com.handybook.handybook.core.constant.BundleKeys;
import com.handybook.handybook.core.ui.activity.LoginActivity;
import com.handybook.handybook.core.ui.activity.SplashActivity;
import com.handybook.handybook.helpcenter.ui.activity.HelpActivity;
import com.handybook.handybook.proteam.ui.activity.ProTeamActivity;
import com.handybook.handybook.referral.ui.ReferralActivity;

import javax.inject.Inject;

//TODO: this needs to be fixed to completely work with new bottom nav
public class DeepLinkIntentProvider
{
    //TODO: clean this up
    //TODO: can split this out so that each module has a routes file
    //TODO: put in properties?
    public static final String DEEP_LINK_BASE_URL = "handybook://deep_link/";
    //FIXME: If the handy:// is not used anywhere, let's remove it from here
    private static final String DEEP_LINK_NEW_BASE_URL = "handy://";
    private static final String DEEP_LINK_SIDE_MENU_URL = DEEP_LINK_BASE_URL + "side_menu/";
    private static UserManager sUserManager;
    private static ConfigurationManager sConfigurationManager;

    //TODO: REALLY don't want to make this static. can the filter be on the activity instead?
    //the deep link library requires that the annotated methods below be static

    @Inject
    public DeepLinkIntentProvider(
            @NonNull UserManager userManager,
            @NonNull ConfigurationManager configurationManager
    )
    {
        sUserManager = userManager;
        sConfigurationManager = configurationManager;
    }

    //TODO: move somewhere else
    //TODO: would rather not make user manager static, but annotated methods below are required to be by library
    public static boolean isUserLoggedIn()
    {
        return sUserManager != null && sUserManager.isUserLoggedIn();
    }

    @DeepLink({
            DEEP_LINK_BASE_URL + "home",
            DEEP_LINK_BASE_URL + "normal_flow",
            DEEP_LINK_BASE_URL + "promo_applied",
            DEEP_LINK_NEW_BASE_URL + "home"})
    public static Intent getHomeIntent(Context context)
    {
        Intent intent = new Intent(context, SplashActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK
                                | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        return intent;
    }

    //TODO update this to handle bottom nav arch
    public static Intent getLoginIntent(Context context, Class<?> destinationClass)
    {
        return getLoginIntent(context, destinationClass, null);
    }

    private static Intent getLoginIntent(Context context, Class<?> destinationClass, Bundle extras)
    {
        Intent intent = new Intent(context, LoginActivity.class);
        intent.putExtras(extras); //TODO test if this can be null
        intent.putExtra(BundleKeys.ACTIVITY, destinationClass.getName());
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK
                                | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        return intent;
    }

    @DeepLink({DEEP_LINK_SIDE_MENU_URL + "promo",
            DEEP_LINK_NEW_BASE_URL + "promo"})
    public static Intent getPromoIntent(Context context)
    {
        return new Intent(context, PromosActivity.class);
    }

    @DeepLink({DEEP_LINK_SIDE_MENU_URL + "mybookings",
            DEEP_LINK_NEW_BASE_URL + "bookings"})
    public static Intent getMyBookingsIntent(Context context)
    {
        if(sConfigurationManager.getPersistentConfiguration().isBottomNavEnabled())
        {
            if(isUserLoggedIn())
            {
                return createBottomNavActivityIntent(context, MainNavTab.BOOKINGS);
            }
            else
            {
                return createBottomNavLoginActivityIntent(context, MainNavTab.BOOKINGS);
            }
        }
        else
        {
            if(isUserLoggedIn())
            {
                return new Intent(context, BookingsActivity.class);
            }
            else
            {
                return getLoginIntent(context, BookingsActivity.class);
            }
        }
    }

    @DeepLink({DEEP_LINK_SIDE_MENU_URL + "past_bookings",
            DEEP_LINK_NEW_BASE_URL + "past_bookings"})
    public static Intent getPastBookingsIntent(Context context)
    {
        //TODO account for bottom nav?
        if (isUserLoggedIn())
        {
            return new Intent(context, HistoryActivity.class);
        }
        return getLoginIntent(context, HistoryActivity.class);
    }

    @DeepLink({DEEP_LINK_SIDE_MENU_URL + "mybookings/id/{" + BundleKeys.BOOKING_ID + "}",
            DEEP_LINK_NEW_BASE_URL + "booking/id/{" + BundleKeys.BOOKING_ID + "}"})
    public static Intent getMyBookingDetailsIntent(Context context)
    {
        if (isUserLoggedIn())
        {
            return new Intent(context, BookingDetailActivity.class);
        }
        return getLoginIntent(context, BookingDetailActivity.class);
    }

    @DeepLink({DEEP_LINK_SIDE_MENU_URL + "account",
            DEEP_LINK_NEW_BASE_URL + "account",
            DEEP_LINK_BASE_URL + "account"})
    public static Intent getAccountIntent(Context context)
    {
        if(sConfigurationManager.getPersistentConfiguration().isBottomNavEnabled())
        {
            if(isUserLoggedIn())
            {
                return createBottomNavActivityIntent(context, MainNavTab.ACCOUNT);
            }
            else
            {
                return createBottomNavLoginActivityIntent(context, MainNavTab.ACCOUNT);
            }
        }
        else
        {
            if(isUserLoggedIn())
            {
                return new Intent(context, ProfileActivity.class);
            }
            else
            {
                return getLoginIntent(context, ProfileActivity.class);
            }
        }
    }

    @DeepLink({DEEP_LINK_SIDE_MENU_URL + "help",
            DEEP_LINK_NEW_BASE_URL + "help"})
    public static Intent getHelpIntent(Context context)
    {
        return new Intent(context, HelpActivity.class);
    }

    @DeepLink({DEEP_LINK_BASE_URL + "pro_team",
            DEEP_LINK_NEW_BASE_URL + "pro_team"})
    public static Intent getProTeamIntent(Context context)
    {
        //TODO need to handle case in which user not logged in
        if (sConfigurationManager.getPersistentConfiguration().isBottomNavEnabled())
        {
            return createBottomNavActivityIntent(context, MainNavTab.PRO_TEAM);
        }
        else
        {
            return new Intent(context, ProTeamActivity.class);
        }
    }

    @DeepLink({DEEP_LINK_BASE_URL + "share",
            DEEP_LINK_NEW_BASE_URL + "share"})
    public static Intent getReferralIntent(Context context)
    {
        //TODO need to handle case in which user not logged in
        if (sConfigurationManager.getPersistentConfiguration().isBottomNavEnabled())
        {
            return createBottomNavActivityIntent(context, MainNavTab.SHARE);
        }
        else
        {
            return new Intent(context, ReferralActivity.class);
        }
    }

    private static Intent createBottomNavActivityIntent(@NonNull Context context, @Nullable MainNavTab mainNavTab)
    {
        Intent intent = new Intent(context, BottomNavActivity.class);
        intent.putExtra(BottomNavActivity.BUNDLE_KEY_TAB, mainNavTab);
        return intent;
    }

    /**
     * TODO give better name
     * creates a login activity intent that launches the bottom nav activity after login,
     * with the given tab as the selected tab
     * @param context
     * @param mainNavTab
     * @return
     */
    private static Intent createBottomNavLoginActivityIntent(@NonNull Context context, @Nullable MainNavTab mainNavTab)
    {
        Bundle extras = new Bundle();
        extras.putSerializable(BottomNavActivity.BUNDLE_KEY_TAB, mainNavTab);
        return getLoginIntent(context, BottomNavActivity.class, extras);
    }
}
