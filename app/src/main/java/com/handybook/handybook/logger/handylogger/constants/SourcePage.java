package com.handybook.handybook.logger.handylogger.constants;

import android.support.annotation.StringDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public interface SourcePage {

    String BOOKING_DETAILS = "booking_details";
    String ACTIVE_BOOKING = "active_booking";
    String MESSAGES = "messages";
    String SHARE = "share_page";
    String RATING = "rating_flow";
    String SIDE_MENU = "side_menu";
    String ACCOUNT = "account";
    String MY_PROS = "my_pros";


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
    @interface HandyLoggerSourcePage {
    }
}
