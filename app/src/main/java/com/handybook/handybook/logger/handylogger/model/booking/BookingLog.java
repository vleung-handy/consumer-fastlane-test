package com.handybook.handybook.logger.handylogger.model.booking;

import com.google.gson.annotations.SerializedName;
import com.handybook.handybook.logger.handylogger.model.EventLog;

/**
 */
public class BookingLog extends EventLog {

    private static final String EVENT_CONTEXT = "booking";

    public BookingLog(final String eventType) {
        super(eventType, EVENT_CONTEXT);
    }

    public static class BookingCancelWarningShown extends BookingLog {

        private static final String EVENT_TYPE = "skip_commitment_warning_shown";

        @SerializedName("booking_id")
        private String mBookingId;

        public BookingCancelWarningShown(final String bookingId) {
            super(EVENT_TYPE);
            mBookingId = bookingId;
        }
    }
}
