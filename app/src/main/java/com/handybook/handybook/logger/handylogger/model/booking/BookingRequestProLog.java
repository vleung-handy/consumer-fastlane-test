package com.handybook.handybook.logger.handylogger.model.booking;

import com.handybook.handybook.logger.handylogger.model.EventLog;

public abstract class BookingRequestProLog extends EventLog {

    private static final String EVENT_CONTEXT = "request_pro";

    public BookingRequestProLog(final String eventType) {
        super(eventType, EVENT_CONTEXT);
    }

    public static class BookingRequestProShownLog extends BookingRequestProLog {

        private static final String EVENT_TYPE = "shown";

        public BookingRequestProShownLog() {
            super(EVENT_TYPE);
        }
    }
}
