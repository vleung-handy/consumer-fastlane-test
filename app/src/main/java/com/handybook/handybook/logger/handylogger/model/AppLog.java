package com.handybook.handybook.logger.handylogger.model;


import com.google.gson.annotations.SerializedName;

public abstract class AppLog extends EventLog
{
    private static final String EVENT_CONTEXT = "app";

    public AppLog(final String eventType)
    {
        super(eventType, EVENT_CONTEXT);
    }


    public static class AppOpenLog extends AppLog
    {
        private static final String EVENT_TYPE = "open";

        @SerializedName("first_launch")
        private boolean mFirstLaunch;
        @SerializedName("new_open")
        private boolean mNewOpen;

        public AppOpenLog(final boolean firstLaunch, final boolean newOpen)
        {
            super(EVENT_TYPE);
            mFirstLaunch = firstLaunch;
            mNewOpen = newOpen;
        }
    }


    public static class AppProteamConversationLog extends AppLog
    {
        private static final String EVENT_TYPE = "navigation";

        @SerializedName("page")
        private String mPage;

        public AppProteamConversationLog()
        {
            super(EVENT_TYPE);
            mPage = "pro_team_conversations";
        }
    }
}
