package com.handybook.handybook.model.logging.booking;


import com.handybook.handybook.model.logging.EventLog;

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
