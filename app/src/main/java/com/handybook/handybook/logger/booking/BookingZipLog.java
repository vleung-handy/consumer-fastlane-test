package com.handybook.handybook.logger.booking;

import com.handybook.handybook.logger.EventLog;

public abstract class BookingZipLog extends EventLog
{
    private static final String EVENT_CONTEXT = "zip";

    public BookingZipLog(final String eventType)
    {
        super(eventType, EVENT_CONTEXT);
    }


    public static class ZipShownLog extends BookingZipLog
    {
        private static final String EVENT_TYPE = "shown";

        public ZipShownLog()
        {
            super(EVENT_TYPE);
        }
    }
}
