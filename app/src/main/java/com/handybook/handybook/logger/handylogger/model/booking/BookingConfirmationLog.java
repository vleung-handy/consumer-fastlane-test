package com.handybook.handybook.logger.handylogger.model.booking;

import com.handybook.handybook.logger.handylogger.model.EventLog;

public abstract class BookingConfirmationLog extends EventLog {

    private static final String EVENT_CONTEXT = "booking_confirmation";

    public BookingConfirmationLog(final String eventType) {
        super(eventType, EVENT_CONTEXT);
    }

    public static class BookingConfirmationShownLog extends BookingConfirmationLog {

        private static final String EVENT_TYPE = "shown";

        public BookingConfirmationShownLog() {
            super(EVENT_TYPE);
        }
    }
}
