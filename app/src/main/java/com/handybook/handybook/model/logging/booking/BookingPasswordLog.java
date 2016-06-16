package com.handybook.handybook.model.logging.booking;

import com.handybook.handybook.model.logging.EventLog;

public class BookingPasswordLog extends EventLog
{
    private static final String EVENT_CONTEXT = "post_booking_password";

    public BookingPasswordLog(final String eventType)
    {
        super(eventType, EVENT_CONTEXT);
    }

    public static class BookingPasswordShownLog extends BookingPasswordLog
    {
        private static final String EVENT_TYPE = "shown";

        public BookingPasswordShownLog()
        {
            super(EVENT_TYPE);
        }
    }
}
