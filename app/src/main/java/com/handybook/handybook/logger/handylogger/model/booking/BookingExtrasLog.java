package com.handybook.handybook.logger.handylogger.model.booking;


import com.handybook.handybook.logger.handylogger.model.EventLog;

public class BookingExtrasLog extends EventLog
{
    private static final String EVENT_CONTEXT = "extras";

    public BookingExtrasLog(final String eventType)
    {
        super(eventType, EVENT_CONTEXT);
    }


    public static class BookingExtrasShownLog extends BookingExtrasLog
    {
        private static final String EVENT_TYPE = "shown";

        public BookingExtrasShownLog()
        {
            super(EVENT_TYPE);
        }
    }
}
