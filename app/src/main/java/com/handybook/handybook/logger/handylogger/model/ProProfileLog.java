package com.handybook.handybook.logger.handylogger.model;

import android.support.annotation.StringDef;

import com.google.gson.annotations.SerializedName;
import com.handybook.handybook.logger.handylogger.constants.SourcePage;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

//todo rename stuff and reorganize
public abstract class ProProfileLog extends EventLog {

    private static final String EVENT_CONTEXT = "pro_profile";

    @SerializedName("provider_id")
    private final String mProviderId;

    public ProProfileLog(final String eventType, final String providerId) {
        super(eventType, EVENT_CONTEXT);
        mProviderId = providerId;
    }

    public static class Shown extends ProProfileLog {

        private static final String EVENT_TYPE = "profile_opened";

        @SerializedName("page_open_source")
        private final String mPageOpenSource;

        public Shown(final String providerId, @SourcePage.HandyLoggerSourcePage String sourcePage) {
            super(EVENT_TYPE, providerId);
            mPageOpenSource = sourcePage;
        }
    }


    public static class ActionButtonClicked extends ProProfileLog {

        private static final String EVENT_TYPE = "cta_tapped";

        public static final String ACTION_BUTTON_TYPE_MESSAGE = "message";
        public static final String ACTION_BUTTON_TYPE_BOOK = "book";
        public static final String ACTION_BUTTON_TYPE_RECOMMEND = "recommend";


        @Retention(RetentionPolicy.SOURCE)
        @StringDef({
                           ACTION_BUTTON_TYPE_MESSAGE,
                           ACTION_BUTTON_TYPE_BOOK,
                           ACTION_BUTTON_TYPE_RECOMMEND
                   })
        public @interface ProProfileActionButtonType {
        }


        @SerializedName("cta_type")
        private final String mActionButtonType;

        public ActionButtonClicked(final String providerId, @ProProfileActionButtonType String actionButtonType) {
            super(EVENT_TYPE, providerId);
            mActionButtonType = actionButtonType;
        }
    }


    public static class TabPageToggled extends ProProfileLog {

        private static final String EVENT_TYPE = "page_toggled";

        public static final String PAGE_FIVE_STAR_REVIEWS = "5_star_reviews";
        public static final String PAGE_ABOUT = "about";


        @Retention(RetentionPolicy.SOURCE)
        @StringDef({
                           PAGE_FIVE_STAR_REVIEWS,
                           PAGE_ABOUT
                   })
        public @interface ProProfileTabPageType {
        }


        @SerializedName("page_selected")
        private final String mSelectedTabPage;

        public TabPageToggled(final String providerId, @ProProfileTabPageType String tabPageType) {
            super(EVENT_TYPE, providerId);
            mSelectedTabPage = tabPageType;
        }
    }


    public static class PageClosed extends ProProfileLog {

        private static final String EVENT_TYPE = "profile_closed";

        public PageClosed(final String providerId) {
            super(EVENT_TYPE, providerId);
        }
    }
}
