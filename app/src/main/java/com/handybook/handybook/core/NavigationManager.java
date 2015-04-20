package com.handybook.handybook.core;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import com.handybook.handybook.data.PropertiesReader;
import com.handybook.handybook.ui.widget.CTANavigationData;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.inject.Inject;

/**
 * Created by cdavis on 4/20/15.
 */
public final class NavigationManager {

    //Valid Action Ids, must have 1-1 with a deep link id
    private static final String ACTION_ID_SERVICES = "TEST_services";
    private static final String DEEP_LINK_ID_SERVICES = "TEST_deeplink_services";

    public static final Map<String, String> ACTION_ID_TO_DEEP_LINK_ID;
    static {
        Map<String, String> map = new HashMap<String, String>();
        map.put(ACTION_ID_SERVICES, DEEP_LINK_ID_SERVICES);

        ACTION_ID_TO_DEEP_LINK_ID = Collections.unmodifiableMap(map);
    }

    private final Properties config;
    private final String baseUrl;
    private final String baseUrlInternal;
    private final Context context;

    @Inject
    public NavigationManager(Context context) {
        this.config = PropertiesReader.getProperties(context, "config.properties");
        baseUrl = config.getProperty("base_url");
        baseUrlInternal = config.getProperty("base_url_internal");
        this.context = context;
    }

    public Boolean navigateTo(CTANavigationData navData)
    {
        Boolean success = false;

        String deepLinkId = actionIdToDeepLinkId(navData.navigationActionId);
        String constructedUrl = constructWebUrlFromNodeUrl(navData.nodeContentWebUrl);

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

    private String constructWebUrlFromNodeUrl(String partialWebUrl)
    {
        return this.baseUrl + partialWebUrl + getAuthToken();
    }

    private String getAuthToken()
    {
        return "";
    }

    //Open external web page
    private void navigateToWeb(String webUrl)
    {
        System.out.println("NAVIGATE TO WEB : " + webUrl);
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(webUrl));
        browserIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        this.context.startActivity(browserIntent);
    }

    private String actionIdToDeepLinkId(String actionId)
    {
       if(ACTION_ID_TO_DEEP_LINK_ID.containsKey(actionId))
       {
           return ACTION_ID_TO_DEEP_LINK_ID.get(actionId);
       }
       return "";
    }

    private void navigateToDeepLink(String deepLinkId)
    {
       //TODO:
        System.out.println("NAVIGATE TO DEEP LINK : " + deepLinkId);
    }

    private Boolean validateDeepLink (String deepLinkId)
    {
        if(deepLinkId == null
           || deepLinkId.isEmpty())
        {
            return false;
        }
        return true;
    }

    private Boolean validateUrl (String webUrl)
    {
        if(webUrl == null
           || webUrl.isEmpty())
        {
            return false;
        }
        return true;
    }



}
