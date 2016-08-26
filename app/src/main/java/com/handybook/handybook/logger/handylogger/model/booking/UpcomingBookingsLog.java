package com.handybook.handybook.logger.handylogger.model.booking;

import com.handybook.handybook.logger.handylogger.model.EventLog;


public class UpcomingBookingsLog extends EventLog
{
    private static final String EVENT_CONTEXT = "upcoming_bookings";

    public UpcomingBookingsLog(final String eventType)
    {
        super(eventType, EVENT_CONTEXT);
    }

    public static class UpcomingBookingsShareButtonPressedLog extends UpcomingBookingsLog
    {
        private static final String EVENT_TYPE = "share_button_pressed";

        public UpcomingBookingsShareButtonPressedLog()
        {
            super(EVENT_TYPE);
        }
    }


    public static class AddBookingPressedLog extends UpcomingBookingsLog
    {
        private static final String EVENT_TYPE = "add_booking_tapped";

        public AddBookingPressedLog()
        {
            super(EVENT_TYPE);
        }
    }
}
