package com.handybook.handybook.logger.handylogger.model;


import com.google.gson.annotations.SerializedName;

public class HandybookDefaultLog extends EventLog
{
    private static final String EVENT_CONTEXT = "hbk_default";

    public HandybookDefaultLog(final String eventType)
    {
        super(eventType, EVENT_CONTEXT);
    }

    public static class AllServicesPageShownLog extends HandybookDefaultLog
    {
        private static final String EVENT_TYPE = "all_services_page_shown";

        public AllServicesPageShownLog()
        {
            super(EVENT_TYPE);
        }
    }


    public static class AllServicesPageSubmittedLog extends HandybookDefaultLog
    {
        private static final String EVENT_TYPE = "all_services_page_submitted";

        @SerializedName("service_id")
        private int mServiceId;

        public AllServicesPageSubmittedLog(final int serviceId)
        {
            super(EVENT_TYPE);
            mServiceId = serviceId;
        }
    }

    public static class SubServicePageShownLog extends HandybookDefaultLog
    {
        private static final String EVENT_TYPE = "sub_service_page_shown";

        @SerializedName("service_id")
        private int mServiceId;

        public SubServicePageShownLog(final int serviceId)
        {
            super(EVENT_TYPE);
            mServiceId = serviceId;
        }
    }

    public static class SubServicePageSubmittedLog extends HandybookDefaultLog
    {
        private static final String EVENT_TYPE = "sub_service_page_submitted";

        @SerializedName("service_id")
        private int mServiceId;

        public SubServicePageSubmittedLog(final int serviceId)
        {
            super(EVENT_TYPE);
            mServiceId = serviceId;
        }
    }
}
