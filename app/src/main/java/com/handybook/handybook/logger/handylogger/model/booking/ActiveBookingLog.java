package com.handybook.handybook.logger.handylogger.model.booking;

import android.support.annotation.StringDef;

import com.google.gson.annotations.SerializedName;
import com.handybook.handybook.logger.handylogger.model.EventLog;

/**
 */
public class ActiveBookingLog extends EventLog {

    private static final String EVENT_CONTEXT = "active_booking";

    @SerializedName("booking_id")
    private String mBookingId;

    public ActiveBookingLog(final String eventType, final String bookingId) {
        super(eventType, EVENT_CONTEXT);
        mBookingId = bookingId;
    }

    /**
     * Trigger: User taps the call or text pro button
     */
    public static class BookingProContactedLog extends ActiveBookingLog {

        private static final String EVENT_TYPE = "booking_pro_contacted";

        public static final String PHONE = "phone";
        public static final String SMS = "sms";
        public static final String CHAT = "chat";


        @StringDef({PHONE, SMS, CHAT})
        public @interface Type {}


        @SerializedName("pro_contact_type")
        private String mContactType;

        public BookingProContactedLog(final String bookingId, @Type final String contactType) {
            super(EVENT_TYPE, bookingId);
            mContactType = contactType;
        }
    }


    /**
     * Trigger: User taps the Report an Issue button
     */
    public static class ReportIssueTappedLog extends ActiveBookingLog {

        private static final String EVENT_TYPE = "report_issue_tapped";

        public ReportIssueTappedLog(final String bookingId) {
            super(EVENT_TYPE, bookingId);
        }
    }


    /**
     * Trigger: User taps to view booking details
     */
    public static class BookingDetailsTappedLog extends ActiveBookingLog {

        private static final String EVENT_TYPE = "booking_details_tapped";

        public BookingDetailsTappedLog(final String bookingId) {
            super(EVENT_TYPE, bookingId);
        }
    }


}
