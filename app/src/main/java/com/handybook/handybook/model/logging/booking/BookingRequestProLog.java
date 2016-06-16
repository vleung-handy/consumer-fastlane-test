package com.handybook.handybook.model.logging.booking;

import com.handybook.handybook.model.logging.EventLog;

public class BookingRequestProLog extends EventLog
{
    private static final String EVENT_CONTEXT = "request_pro";

    public BookingRequestProLog(final String eventType)
    {
        super(eventType, EVENT_CONTEXT);
    }


    public static class BookingRequestProShownLog extends BookingRequestProLog
    {
        private static final String EVENT_TYPE = "shown";

        public BookingRequestProShownLog()
        {
            super(EVENT_TYPE);
        }
    }
}
