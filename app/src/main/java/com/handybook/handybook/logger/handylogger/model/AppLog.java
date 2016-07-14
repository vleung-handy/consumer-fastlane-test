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

        public AppOpenLog(final boolean firstLaunch)
        {
            super(EVENT_TYPE);
            mFirstLaunch = firstLaunch;
        }
    }
}
