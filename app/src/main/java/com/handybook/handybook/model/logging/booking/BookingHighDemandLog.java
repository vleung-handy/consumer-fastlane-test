package com.handybook.handybook.model.logging.booking;


import com.handybook.handybook.model.logging.EventLog;

public abstract class BookingHighDemandLog extends EventLog
{
    private static final String EVENT_CONTEXT = "high_demand";

    public BookingHighDemandLog(final String eventType)
    {
        super(eventType, EVENT_CONTEXT);
    }

    public static class BookingHighDemandShownLog extends BookingHighDemandLog
    {
        private static final String EVENT_TYPE = "shown";

        public BookingHighDemandShownLog()
        {
            super(EVENT_TYPE);
        }
    }
}
