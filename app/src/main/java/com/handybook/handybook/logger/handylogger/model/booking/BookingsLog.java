package com.handybook.handybook.logger.handylogger.model.booking;

import android.support.annotation.StringDef;

import com.google.gson.annotations.SerializedName;
import com.handybook.handybook.logger.handylogger.model.EventLog;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import static com.handybook.handybook.logger.handylogger.model.booking.BookingsLog.PageToggled.Page.PAST_BOOKINGS;
import static com.handybook.handybook.logger.handylogger.model.booking.BookingsLog.PageToggled.Page.UPCOMING_BOOKINGS;

/**
 * logger for the combined upcoming/past bookings
 */
public abstract class BookingsLog extends EventLog {

    private static final String EVENT_CONTEXT = "bookings";

    public BookingsLog(final String eventType) {
        super(eventType, EVENT_CONTEXT);
    }

    public static class PageToggled extends BookingsLog {

        /*
        note that these values are different from the ones in
        AppLog.AppNavigationLog but we cannot yet consolidate them for consistency
        because iOS currently has these values
         */
        public static class Page {

            public static final String UPCOMING_BOOKINGS = "upcoming";
            public static final String PAST_BOOKINGS = "past";
        }


        @Retention(RetentionPolicy.SOURCE)
        @StringDef({UPCOMING_BOOKINGS, PAST_BOOKINGS})
        public @interface BookingsLogPage {
        }


        private static final String EVENT_TYPE = "page_selected";
        @SerializedName("page_selected")
        private final String mPageSelected;

        public PageToggled(@BookingsLogPage String pageSelected) {
            super(EVENT_TYPE);
            mPageSelected = pageSelected;
        }
    }
}
