package com.handybook.handybook.logger.handylogger.model.booking;

import com.handybook.handybook.logger.handylogger.model.EventLog;

public class BookingQuoteLog extends EventLog
{
    private static final String EVENT_CONTEXT = "quote";

    public BookingQuoteLog(final String eventType)
    {
        super(eventType, EVENT_CONTEXT);
    }


    public static class BookingQuoteShownLog extends BookingQuoteLog
    {
        private static final String EVENT_TYPE = "shown";

        public BookingQuoteShownLog()
        {
            super(EVENT_TYPE);
        }
    }
}
