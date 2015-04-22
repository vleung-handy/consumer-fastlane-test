package com.handybook.handybook.core;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import com.google.android.gms.analytics.ecommerce.Promotion;
import com.handybook.handybook.data.DataManager;
import com.handybook.handybook.data.DataManagerErrorHandler;
import com.handybook.handybook.data.Mixpanel;
import com.handybook.handybook.data.PropertiesReader;
import com.handybook.handybook.ui.activity.BookingDateActivity;
import com.handybook.handybook.ui.activity.BookingsActivity;
import com.handybook.handybook.ui.activity.ProfileActivity;
import com.handybook.handybook.ui.activity.PromosActivity;
import com.handybook.handybook.ui.activity.ServiceCategoriesActivity;
import com.handybook.handybook.ui.activity.SplashActivity;
import com.handybook.handybook.ui.widget.CTANavigationData;

import java.util.Collections;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.inject.Inject;

/**
 * Created by cdavis on 4/20/15.
 */
public final class NavigationManager {

    //Injected params
    private UserManager userManager;

    //Valid Action Ids
    private static final String ACTION_ID_SERVICES = "services";
    private static final String ACTION_ID_RESCHEDULE = "reschedule";
    private static final String ACTION_ID_CANCEL_BOOKING = "cancel_booking";
    private static final String ACTION_ID_RATE_PRO = "rate_pro";
    private static final String ACTION_ID_REQUEST_PRO = "request_pro";
    private static final String ACTION_ID_GO_TO_MY_BOOKINGS = "go_to_my_bookings";
    private static final String ACTION_ID_GO_TO_MY_PROFILE = "go_to_my_profile";
    private static final String ACTION_ID_GO_TO_REFERRALS = "go_to_referrals";
    private static final String ACTION_ID_EDIT_ENTRY_INFO = "edit_entry_info";
    private static final String ACTION_ID_EDIT_NOTE_TO_PRO = "edit_note_to_pro";
    private static final String ACTION_ID_EDIT_HOURS = "edit_hours";
    private static final String ACTION_ID_EDIT_FREQUENCY = "edit_frequency";
    private static final String ACTION_ID_GET_INVOICE = "get_invoice";
    private static final String ACTION_ID_ATTACH_LAUNDRY = "attach_laundry";
    private static final String ACTION_ID_SKIP_LAUNDRY = "skip_laundry";
    private static final String ACTION_ID_SCHEDULE_PICKUP = "schedule_pickup";
    private static final String ACTION_ID_SCHEDULE_DELIVERY = "schedule_delivery";
    private static final String ACTION_ID_SCHEDULE_CONFLICT = "schedule_conflict";
    private static final String ACTION_ID_MOVING_HELP = "book_moving_help";

    //Valid Deeplink ids
    private static final String DEEP_LINK_ID_SERVICES = "services/";
    private static final String DEEP_LINK_ID_PROFILE = "profile/";
    private static final String DEEP_LINK_ID_BOOKINGS = "bookings/";
    private static final String DEEP_LINK_ID_PROMOTIONS = "promotions/";


    //Action Id to Deeplink Id Mapping
    public static final Map<String, String> ACTION_ID_TO_DEEP_LINK_ID;
    static {
        Map<String, String> map = new HashMap<String, String>();

        map.put(ACTION_ID_SERVICES, DEEP_LINK_ID_SERVICES);
        map.put(ACTION_ID_GO_TO_MY_PROFILE, DEEP_LINK_ID_PROFILE);
        map.put(ACTION_ID_GO_TO_MY_BOOKINGS, DEEP_LINK_ID_BOOKINGS);

        ACTION_ID_TO_DEEP_LINK_ID = Collections.unmodifiableMap(map);
    }

    //Member fields
    private final Properties config;
    private final String baseUrl;
    private final String baseUrlInternal;
    private final Context context;

    ///////
    //Public
    ///////

    @Inject
    public NavigationManager(Context context, UserManager userManager)
    {
        this.config = PropertiesReader.getProperties(context, "config.properties");
        baseUrl = config.getProperty("base_url");
        baseUrlInternal = config.getProperty("base_url_internal");
        this.context = context;
        this.userManager = userManager;
    }

    //Use navigation data to attempt to navigate to a deep link, using web url as fallback if deeplink is not mapped
    public Boolean navigateTo(CTANavigationData navData)
    {
        Boolean success = false;

        String deepLinkId = actionIdToDeepLinkId(navData.navigationActionId);
        String constructedUrl = constructWebUrlFromNodeUrl(navData.nodeContentWebUrl);

        //Deep links have priority over web links
        if(validateDeepLink(deepLinkId))
        {
            navigateToDeepLink(deepLinkId);
            success = true;
        }
        else if(validateUrl(constructedUrl))
        {
            navigateToWeb(constructedUrl);
            success = true;
        }

        return success;
    }

    //Handle splash screen deep links
    public void handleSplashScreenLaunch(Intent splashScreenIntent)
    {
        final String action = splashScreenIntent.getAction();
        final Uri data = splashScreenIntent.getData();

        if (!action.equals("android.intent.action.VIEW") || !data.getScheme().equals("handy")) {
            openServiceCategoriesActivity();
            return;
        }

        final String deepLinkId = data.getHost() + data.getPath();
        navigateToDeepLink(deepLinkId);
    }

    ///////
    //Private
    ///////

    private String constructWebUrlFromNodeUrl(String partialWebUrl)
    {
        //TODO: Update this, need the real formatting
        return this.baseUrl + partialWebUrl + getAuthToken();
    }

    private String getAuthToken()
    {
        return (userManager.getCurrentUser() != null ? userManager.getCurrentUser().getAuthToken() : "");
    }

    //Open external web page
    private void navigateToWeb(String webUrl)
    {
        //System.out.println("NAVIGATE TO WEB : " + webUrl);

        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(webUrl));
        browserIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(browserIntent);
    }

    private String actionIdToDeepLinkId(String actionId)
    {
       if(ACTION_ID_TO_DEEP_LINK_ID.containsKey(actionId))
       {
           return ACTION_ID_TO_DEEP_LINK_ID.get(actionId);
       }
       return "";
    }

    private Boolean validateDeepLink (String deepLinkId)
    {
        if(deepLinkId == null || deepLinkId.isEmpty())
        {
            return false;
        }
        return true;
    }

    private Boolean validateUrl (String webUrl)
    {
        if(webUrl == null || webUrl.isEmpty())
        {
            return false;
        }
        return true;
    }

    private void navigateToDeepLink(String deepLinkId)
    {
        //System.out.println("NAVIGATE TO DEEP LINK : " + deepLinkId);

        switch (deepLinkId)
        {
            case DEEP_LINK_ID_PROFILE:
                openActivity(ProfileActivity.class, true);
                break;

            case DEEP_LINK_ID_BOOKINGS:
                openActivity(BookingsActivity.class, true);
                break;

            case DEEP_LINK_ID_SERVICES:
                openServiceCategoriesActivity();
                break;

            case DEEP_LINK_ID_PROMOTIONS:
                openActivity(PromosActivity.class, true);
                break;

            default:
                openServiceCategoriesActivity();
        }
    }

    public void startActivity(final Intent intent)
    {
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK
                | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        this.context.startActivity(intent);
    }

    //Open deep links
    private void openActivity(final Class<? extends Activity> targetClass, final boolean requiresUser) {
        if (requiresUser && userManager.getCurrentUser() == null) {
            openServiceCategoriesActivity();
        }
        startActivity(new Intent(this.context, targetClass));
    }

    private void openActivity(final Class<? extends Activity> targetClass, final HashMap<String, String> params) {
        Intent navIntent = new Intent(this.context, targetClass);

        startActivity(new Intent(this.context, targetClass));
    }

    private void openServiceCategoriesActivity() {
        startActivity(new Intent(this.context, ServiceCategoriesActivity.class));
    }

}
