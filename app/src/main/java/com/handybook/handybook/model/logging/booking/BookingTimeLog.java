package com.handybook.handybook.model.logging.booking;


import com.handybook.handybook.model.logging.EventLog;

public class BookingTimeLog extends EventLog
{
    private static final String EVENT_CONTEXT = "time";

    public BookingTimeLog(final String eventType)
    {
        super(eventType, EVENT_CONTEXT);
    }

    public static class BookingTimeShownLog extends BookingTimeLog
    {
        private static final String EVENT_TYPE = "shown";

        public BookingTimeShownLog()
        {
            super(EVENT_TYPE);
        }
    }
}
