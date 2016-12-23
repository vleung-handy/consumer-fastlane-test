package com.handybook.handybook.deeplink;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;

import com.airbnb.deeplinkdispatch.DeepLink;
import com.handybook.handybook.account.ui.ProfileActivity;
import com.handybook.handybook.booking.history.HistoryActivity;
import com.handybook.handybook.booking.ui.activity.BookingDetailActivity;
import com.handybook.handybook.booking.ui.activity.BookingsActivity;
import com.handybook.handybook.booking.ui.activity.PromosActivity;
import com.handybook.handybook.configuration.manager.ConfigurationManager;
import com.handybook.handybook.core.Tab;
import com.handybook.handybook.core.UserManager;
import com.handybook.handybook.core.constant.BundleKeys;
import com.handybook.handybook.core.ui.activity.BottomNavActivity;
import com.handybook.handybook.core.ui.activity.LoginActivity;
import com.handybook.handybook.core.ui.activity.SplashActivity;
import com.handybook.handybook.helpcenter.ui.activity.HelpActivity;
import com.handybook.handybook.proteam.ui.activity.ProTeamActivity;
import com.handybook.handybook.referral.ui.ReferralActivity;

import javax.inject.Inject;

//TODO: package this better
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

    public static Intent getLoginIntent(Context context, Class<?> destinationClass)
    {
        Intent intent = new Intent(context, LoginActivity.class);
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
        if (isUserLoggedIn())
        {
            if (sConfigurationManager.getPersistentConfiguration().isBottomNavEnabled())
            {
                return createBottomNavActivityIntent(context, Tab.BOOKINGS);
            }
            else
            {
                return new Intent(context, BookingsActivity.class);
            }
        }
        return getLoginIntent(context, BookingsActivity.class);
    }

    @DeepLink({DEEP_LINK_SIDE_MENU_URL + "past_bookings",
            DEEP_LINK_NEW_BASE_URL + "past_bookings"})
    public static Intent getPastBookingsIntent(Context context)
    {
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
        if (isUserLoggedIn())
        {
            if (sConfigurationManager.getPersistentConfiguration().isBottomNavEnabled())
            {
                return createBottomNavActivityIntent(context, Tab.ACCOUNT);
            }
            else
            {
                return new Intent(context, ProfileActivity.class);
            }
        }
        return getHomeIntent(context);
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
        if (sConfigurationManager.getPersistentConfiguration().isBottomNavEnabled())
        {
            return createBottomNavActivityIntent(context, Tab.PRO_TEAM);
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
        if (sConfigurationManager.getPersistentConfiguration().isBottomNavEnabled())
        {
            return createBottomNavActivityIntent(context, Tab.SHARE);
        }
        else
        {
            return new Intent(context, ReferralActivity.class);
        }
    }

    private static Intent createBottomNavActivityIntent(@NonNull Context context, @NonNull Tab tab)
    {
        Intent intent = new Intent(context, BottomNavActivity.class);
        intent.putExtra(BottomNavActivity.BUNDLE_KEY_TAB, tab);
        return intent;
    }
}
