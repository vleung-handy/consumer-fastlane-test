package com.handybook.handybook.logger.booking;


import com.handybook.handybook.logger.EventLog;

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
