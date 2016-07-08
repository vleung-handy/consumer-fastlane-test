package com.handybook.handybook.logger.booking;

import com.handybook.handybook.logger.EventLog;


public class BookingCommentsLog extends EventLog
{
    private static final String EVENT_CONTEXT = "comments";

    public BookingCommentsLog(final String eventType)
    {
        super(eventType, EVENT_CONTEXT);
    }

    public static class BookingCommentsShownLog extends BookingCommentsLog
    {
        private static final String EVENT_TYPE = "shown";

        public BookingCommentsShownLog()
        {
            super(EVENT_TYPE);
        }
    }
}
