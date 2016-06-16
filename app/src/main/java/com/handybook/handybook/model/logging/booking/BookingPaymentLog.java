package com.handybook.handybook.model.logging.booking;


import com.handybook.handybook.model.logging.EventLog;

public class BookingPaymentLog extends EventLog
{
    private static final String EVENT_CONTEXT = "payment";

    public BookingPaymentLog(final String eventType)
    {
        super(eventType, EVENT_CONTEXT);
    }

    public static class BookingPaymentShownLog extends BookingPaymentLog
    {
        private static final String EVENT_TYPE = "shown";

        public BookingPaymentShownLog()
        {
            super(EVENT_TYPE);
        }
    }
}
