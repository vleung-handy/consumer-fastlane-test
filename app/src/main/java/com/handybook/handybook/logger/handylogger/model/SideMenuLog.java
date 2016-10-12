package com.handybook.handybook.logger.handylogger.model;


import com.google.gson.annotations.SerializedName;

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

        @SerializedName("share_button_type")
        private String mShareButtonType;

        private static final String EVENT_TYPE = "share_button_tapped";

        public ShareButtonTappedLog()
        {
            super(EVENT_TYPE);
            mShareButtonType = BANNER;
        }
    }


    public static class HelpCenterTappedLog extends SideMenuLog
    {

        public static final String EVENT_TYPE = "help_center_tapped";

        public HelpCenterTappedLog()
        {
            super(EVENT_TYPE);
        }
    }
}
