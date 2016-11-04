package com.handybook.handybook.logger.handylogger.model.booking;

import com.google.gson.annotations.SerializedName;
import com.handybook.handybook.logger.handylogger.model.EventLog;

/**
 */
public class PastBookingsLog extends EventLog
{
    private static final String EVENT_CONTEXT = "past_bookings";

    public PastBookingsLog(final String eventType)
    {
        super(eventType, EVENT_CONTEXT);
    }

    /**
     * Trigger: User taps to view booking details
     */
    public static class BookingDetailsTappedLog extends PastBookingsLog
    {
        private static final String EVENT_TYPE = "booking_details_tapped";

        @SerializedName("booking_id")
        private String mBookingId;

        public BookingDetailsTappedLog(final String bookingId)
        {
            super(EVENT_TYPE);
            mBookingId = bookingId;
        }
    }


    public static class PastBookingsShareMenuPressedLog extends UpcomingBookingsLog
    {
        private static final String EVENT_TYPE = "share_menu_tapped";

        public PastBookingsShareMenuPressedLog()
        {
            super(EVENT_TYPE);
        }
    }
}
