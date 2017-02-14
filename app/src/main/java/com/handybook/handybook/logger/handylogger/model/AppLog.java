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

    //TODO log in right place
    public abstract static class PersistentPromoLog extends AppLog
    {
        //TODO verify these with PM
        private static final String EVENT_TYPE = "persistent_promo";

        @SerializedName("promo_id")
        private final String mPromoId;

        PersistentPromoLog(String eventSubType, String promoId)
        {
            super(EVENT_TYPE + "_" + eventSubType);
            mPromoId = promoId;
        }

        /**
         * persistent promo preview is shown
         */
        public static class PreviewShown extends PersistentPromoLog
        {
            private static final String EVENT_TYPE = "preview_shown";
            public PreviewShown(String promoId)
            {
                super(EVENT_TYPE, promoId);
            }
        }


        /**
         * persistent promo is fully expanded
         */
        public static class ExpandedViewShown extends PersistentPromoLog
        {
            private static final String EVENT_TYPE = "expanded_view_shown";
            public ExpandedViewShown(String promoId)
            {
                super(EVENT_TYPE, promoId);
            }
        }


        /**
         * persistent promo expanded view action button is clicked
         */
        public static class ExpandedViewActionClicked extends PersistentPromoLog
        {
            private static final String EVENT_TYPE = "expanded_view_action_clicked";
            public ExpandedViewActionClicked(String promoId)
            {
                super(EVENT_TYPE, promoId);
            }
        }
    }
}
