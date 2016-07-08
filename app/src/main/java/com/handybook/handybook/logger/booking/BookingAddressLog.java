package com.handybook.handybook.logger.booking;

import com.handybook.handybook.logger.EventLog;


public class BookingAddressLog extends EventLog
{
    private static final String EVENT_CONTEXT = "address";

    public BookingAddressLog(final String eventType)
    {
        super(eventType, EVENT_CONTEXT);
    }


    public static class BookingAddressShownLog extends BookingAddressLog
    {
        private static final String EVENT_TYPE = "shown";

        public BookingAddressShownLog()
        {
            super(EVENT_TYPE);
        }
    }
}
