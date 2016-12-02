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
        private final boolean mFirstLaunch;
        @SerializedName("new_open")
        private final boolean mNewOpen;

        public AppOpenLog(final boolean firstLaunch, final boolean newOpen)
        {
            super(EVENT_TYPE);
            mFirstLaunch = firstLaunch;
            mNewOpen = newOpen;
        }
    }


    public static class AppNavigationLog extends AppLog
    {
        private static final String EVENT_TYPE = "navigation";

        @SerializedName("page")
        private final String mPage;

        public AppNavigationLog(final String page)
        {
            super(EVENT_TYPE);
            mPage = page;
        }
    }
}
