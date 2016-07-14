package com.handybook.handybook.logger.handylogger.model.booking;

import com.handybook.handybook.logger.handylogger.model.EventLog;

public class BookingQuoteShown extends EventLog
{
    private static final String EVENT_CONTEXT = "quote";

    public BookingQuoteShown(final String eventType)
    {
        super(eventType, EVENT_CONTEXT);
    }


    public static class BookingQuoteShownLog extends BookingQuoteShown
    {
        private static final String EVENT_TYPE = "shown";

        public BookingQuoteShownLog()
        {
            super(EVENT_TYPE);
        }
    }
}
