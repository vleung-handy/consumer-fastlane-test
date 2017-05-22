package com.handybook.handybook.logger.handylogger.model;

import android.support.annotation.NonNull;
import android.support.annotation.StringDef;

import com.google.gson.annotations.SerializedName;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public abstract class AppLog extends EventLog {

    private static final String EVENT_CONTEXT = "app";

    public AppLog(final String eventType) {
        super(eventType, EVENT_CONTEXT);
    }

    public static class AppOpenLog extends AppLog {

        private static final String EVENT_TYPE = "open";

        @SerializedName("first_launch")
        private final boolean mFirstLaunch;
        @SerializedName("new_open")
        private final boolean mNewOpen;

        public AppOpenLog(final boolean firstLaunch, final boolean newOpen) {
            super(EVENT_TYPE);
            mFirstLaunch = firstLaunch;
            mNewOpen = newOpen;
        }
    }


    public static class AppNavigationLog extends AppLog {

        public static class Page {

            public static final String PRO_TEAM_CONVERSATIONS = "pro_team_conversations";
            public static final String MY_PROS = "my_pros";
            public static final String PAST_BOOKINGS = "past_bookings";
            public static final String UPCOMING_BOOKINGS = "upcoming_bookings";
        }


        @Retention(RetentionPolicy.SOURCE)
        @StringDef({
                           Page.PRO_TEAM_CONVERSATIONS,
                           Page.PAST_BOOKINGS,
                           Page.UPCOMING_BOOKINGS,
                           Page.MY_PROS
                   })
        public @interface AppNavigationLogPage {
        }

        private static final String EVENT_TYPE = "navigation";

        @SerializedName("page")
        private final String mPage;

        public AppNavigationLog(
                @AppNavigationLogPage final String page
        ) {
            super(EVENT_TYPE);
            mPage = page;
        }
    }


    public abstract static class PromoLog extends AppLog {

        public static class Type {

            public static final String PERSISTENT = "persistent";
            public static final String SPLASH = "splash";
        }


        @Retention(RetentionPolicy.SOURCE)
        @StringDef({
                           Type.PERSISTENT,
                           Type.SPLASH
                   })
        @interface PromoType {
        }


        private static final String EVENT_TYPE = "promotion";

        @SerializedName("promo_id")
        private final String mPromoId;

        @SerializedName("type")
        private final String mPromoType;

        PromoLog(String eventSubType, String promoId, @PromoType String promoType) {
            super(EVENT_TYPE + "_" + eventSubType);
            mPromoId = promoId;
            mPromoType = promoType;
        }

        /**
         * persistent promo preview is shown
         */
        public static class Shown extends PromoLog {

            private static final String EVENT_TYPE = "shown";

            public Shown(@NonNull String promoId, @NonNull @PromoType String promoType) {
                super(EVENT_TYPE, promoId, promoType);
            }
        }


        /**
         * promo is fully expanded
         */
        public static class FullyExpanded extends PromoLog {

            private static final String EVENT_TYPE = "expanded";

            public FullyExpanded(@NonNull String promoId, @NonNull @PromoType String promoType) {
                super(EVENT_TYPE, promoId, promoType);
            }
        }


        /**
         * promo action button is clicked
         */
        public static class Accepted extends PromoLog {

            private static final String EVENT_TYPE = "accepted";

            public Accepted(@NonNull String promoId, @NonNull @PromoType String promoType) {
                super(EVENT_TYPE, promoId, promoType);
            }
        }


        /**
         * persistent promo is slid down via user interaction
         */
        public static class Previewed extends PromoLog {

            private static final String EVENT_TYPE = "previewed";

            public Previewed(@NonNull String promoId, @NonNull @PromoType String promoType) {
                super(EVENT_TYPE, promoId, promoType);
            }
        }
    }
}
