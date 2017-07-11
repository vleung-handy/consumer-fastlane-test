package com.handybook.handybook.logger.handylogger.model.booking;

import com.google.gson.annotations.SerializedName;
import com.handybook.handybook.logger.handylogger.model.EventLog;

public abstract class IssueResolutionLog extends EventLog {

    private static final String EVENT_CONTEXT = "location_based_issue_resolution";

    @SerializedName("booking_id")
    private String mBookingId;

    public IssueResolutionLog(final String eventType, final String bookingId) {
        super(eventType, EVENT_CONTEXT);
        mBookingId = bookingId;
    }

    public static class ReportIssueOpened extends IssueResolutionLog {

        private static final String EVENT_TYPE = "opened";

        @SerializedName("last_status_shown")
        private String mLastStatusShown;

        public ReportIssueOpened(String bookingId, String lastStatusShown) {
            super(EVENT_TYPE, bookingId);
            mLastStatusShown = lastStatusShown;
        }
    }


    public static class HelpLinkTapped extends IssueResolutionLog {

        private static final String EVENT_TYPE = "help_link_tapped";

        @SerializedName("text")
        private String mText;
        @SerializedName("link")
        private String mLink;

        public HelpLinkTapped(final String bookingId, final String text, final String link) {
            super(EVENT_TYPE, bookingId);
            mText = text;
            mLink = link;
        }
    }
}
