package com.handybook.handybook.logger.handylogger.model;


import com.google.gson.annotations.SerializedName;

public abstract class HelpCenterLog extends EventLog
{
    private static final String EVENT_CONTEXT = "help_center";

    protected HelpCenterLog(final String eventType)
    {
        super(eventType, EVENT_CONTEXT);
    }

    public static class HelpCenterTappedLog extends HelpCenterLog
    {
        public static final String EVENT_TYPE = "help_center_tapped";

        public HelpCenterTappedLog()
        {
            super(EVENT_TYPE);
        }
    }


    public static class BookingDetailsTappedLog extends HelpCenterLog
    {
        public static final String EVENT_TYPE = "booking_details_tapped";

        @SerializedName("booking_id")
        private String mBookingId;

        public BookingDetailsTappedLog(final String bookingId)
        {
            super(EVENT_TYPE);
            mBookingId = bookingId;
        }
    }


    public static class ReportIssueTapped extends HelpCenterLog
    {
        public static final String EVENT_TYPE = "report_issue_tapped";

        @SerializedName("booking_id")
        private String mBookingId;

        public ReportIssueTapped(final String bookingId)
        {
            super(EVENT_TYPE);
            mBookingId = bookingId;
        }
    }


    public static class HelpLinkTappedLog extends HelpCenterLog
    {
        public static final String EVENT_TYPE = "help_link_tapped";

        @SerializedName("text")
        private String mText;
        @SerializedName("link")
        private String mLink;

        public HelpLinkTappedLog(final String text, final String link)
        {
            super(EVENT_TYPE);
            mText = text;
            mLink = link;
        }
    }
}
