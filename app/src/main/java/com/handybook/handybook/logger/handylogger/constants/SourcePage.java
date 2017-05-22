package com.handybook.handybook.logger.handylogger.constants;

import android.support.annotation.StringDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class SourcePage {

    public static final String BOOKING_DETAILS = "booking_details";
    public static final String ACTIVE_BOOKING = "active_booking";
    public static final String MESSAGES = "messages";
    public static final String SHARE = "share_page";
    public static final String RATING = "rating_flow";
    public static final String SIDE_MENU = "side_menu";
    public static final String ACCOUNT = "account";
    public static final String MY_PROS = "my_pros";


    @Retention(RetentionPolicy.SOURCE)
    @StringDef({
                       BOOKING_DETAILS,
                       ACTIVE_BOOKING,
                       MESSAGES,
                       SHARE,
                       RATING,
                       SIDE_MENU,
                       ACCOUNT,
               })
    public @interface HandyLoggerSourcePage {
    }
}
