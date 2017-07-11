package com.handybook.handybook.logger.handylogger.model.booking;

import com.google.gson.annotations.SerializedName;
import com.handybook.handybook.logger.handylogger.model.EventLog;

public abstract class UpcomingBookingsLog extends EventLog {

    private static final String EVENT_CONTEXT = "upcoming_bookings";

    public UpcomingBookingsLog(final String eventType) {
        super(eventType, EVENT_CONTEXT);
    }

    public static class UpcomingBookingsShareMenuPressedLog extends UpcomingBookingsLog {

        private static final String EVENT_TYPE = "share_menu_tapped";

        public UpcomingBookingsShareMenuPressedLog() {
            super(EVENT_TYPE);
        }
    }


    public static class UpcomingBookingsShareBannerTappedLog extends UpcomingBookingsLog {

        private static final String EVENT_TYPE = "share_banner_tapped";

        public UpcomingBookingsShareBannerTappedLog() {
            super(EVENT_TYPE);
        }
    }


    public static class AddBookingPressedLog extends UpcomingBookingsLog {

        private static final String EVENT_TYPE = "add_booking_tapped";

        public AddBookingPressedLog() {
            super(EVENT_TYPE);
        }
    }


    /**
     * Trigger: User taps to view booking details
     */
    public static class BookingDetailsTappedLog extends UpcomingBookingsLog {

        private static final String EVENT_TYPE = "booking_details_tapped";

        @SerializedName("booking_id")
        private String mBookingId;

        public BookingDetailsTappedLog(final String bookingId) {
            super(EVENT_TYPE);
            mBookingId = bookingId;
        }
    }

}
