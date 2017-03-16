package com.handybook.handybook.library.util;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

public class ExternalApplicationLauncher {

    private static final String PLAY_STORE_URL_FORMAT_MARKET = "market://details?id=%1s";
    private static final String PLAY_STORE_URL_FORMAT_WEB
            = "https://play.google.com/store/apps/details?id=%1s";

    public static void launchPlayStoreAppListing(Context context) {
        final String appPackageName = context.getPackageName();
        Uri playUri;
        try {
            playUri = Uri.parse(String.format(PLAY_STORE_URL_FORMAT_MARKET, appPackageName));
            context.startActivity(
                    new Intent(Intent.ACTION_VIEW, playUri)
                            .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
            );
            //This will fail if user doesn't have play store app, hence the link to web below
        }
        catch (android.content.ActivityNotFoundException anfe) {
            playUri = Uri.parse(String.format(PLAY_STORE_URL_FORMAT_WEB, appPackageName));
            context.startActivity(
                    new Intent(Intent.ACTION_VIEW, playUri)
                            .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
            );
        }
    }
}
