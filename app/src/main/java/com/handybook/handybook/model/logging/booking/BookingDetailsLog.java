package com.handybook.handybook.model.logging.booking;

import com.handybook.handybook.model.logging.EventLog;


public class BookingDetailsLog extends EventLog
{
    private static final String EVENT_CONTEXT = "details";

    public BookingDetailsLog(final String eventType)
    {
        super(eventType, EVENT_CONTEXT);
    }

    public static class BookingDetailsShownLog extends BookingDetailsLog
    {
        private static final String EVENT_TYPE = "shown";

        public BookingDetailsShownLog()
        {
            super(EVENT_TYPE);
        }
    }
}

