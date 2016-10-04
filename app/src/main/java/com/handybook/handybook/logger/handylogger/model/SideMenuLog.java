package com.handybook.handybook.logger.handylogger.model;


import android.support.annotation.StringDef;

import com.google.gson.annotations.SerializedName;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public abstract class SideMenuLog extends EventLog
{
    private static final String EVENT_CONTEXT = "side_menu";

    protected SideMenuLog(final String eventType)
    {
        super(eventType, EVENT_CONTEXT);
    }

    public static class ShareButtonTappedLog extends SideMenuLog
    {
        public static final String BANNER = "banner";
        public static final String CTA = "cta";


        @Retention(RetentionPolicy.SOURCE)
        @StringDef({
                BANNER,
                CTA
        })
        @interface ShareButtonType {}


        @SerializedName("share_button_type")
        private String mShareButtonType;

        private static final String EVENT_TYPE = "share_button_tapped";

        public ShareButtonTappedLog(@ShareButtonType final String shareButtonType)
        {
            super(EVENT_TYPE);
            mShareButtonType = shareButtonType;
        }
    }
}
