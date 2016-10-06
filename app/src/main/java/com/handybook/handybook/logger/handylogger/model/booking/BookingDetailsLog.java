package com.handybook.handybook.logger.handylogger.model.booking;

import com.google.gson.annotations.SerializedName;
import com.handybook.handybook.logger.handylogger.model.EventLog;

import java.util.Date;


public abstract class BookingDetailsLog extends EventLog
{
    private static final String EVENT_CONTEXT = "booking_details";

    private BookingDetailsLog(final String eventType)
    {
        super(eventType, EVENT_CONTEXT);
    }

    public static class BookingDetailsShownLog extends BookingDetailsLog
    {
        private static final String EVENT_TYPE = "shown";

        public BookingDetailsShownLog()
        {
            super(EVENT_TYPE);
        }
    }


    public static class RescheduleBooking extends BookingDetailsLog
    {
        private static final String EVENT_TYPE_PREFIX = "reschedule_booking_";

        @SerializedName("booking_id")
        private final String mBookingId;

        @SerializedName("old_date")
        private final Date mOldDate;

        @SerializedName("new_date")
        private final Date mNewDate;

        /**
         * @param eventType has a value of {selected, submitted, success, error}
         * @param bookingId
         * @param oldDate
         * @param newDate
         */
        public RescheduleBooking(final EventType eventType, String bookingId, Date oldDate, Date newDate)
        {
            //arguments passed should look like: reschedule_booking_selected, etc.
            super(EVENT_TYPE_PREFIX + eventType.getValue());

            mBookingId = bookingId;
            mOldDate = oldDate;
            mNewDate = newDate;
        }
    }


    public static class SkipBooking extends BookingDetailsLog
    {
        private static final String EVENT_TYPE_PREFIX = "skip_booking_";

        @SerializedName("booking_id")
        private final String mBookingId;

        public SkipBooking(final EventType eventType, final String bookingId)
        {
            //in the form of skip_booking_selected, etc.
            super(EVENT_TYPE_PREFIX + eventType.getValue());
            mBookingId = bookingId;
        }
    }


    public static class RescheduleInsteadShown extends BookingDetailsLog
    {
        private static final String EVENT_TYPE = "reschedule_instead_shown";

        @SerializedName("booking_id")
        private final String mBookingId;

        public RescheduleInsteadShown(final String bookingId)
        {
            super(EVENT_TYPE);

            mBookingId = bookingId;
        }
    }


    public static class ContinueSkipSelected extends BookingDetailsLog
    {
        private static final String EVENT_TYPE = "continue_skip_selected";

        @SerializedName("booking_id")
        private final String mBookingId;

        public ContinueSkipSelected(final String bookingId)
        {
            super(EVENT_TYPE);
            mBookingId = bookingId;
        }
    }


    public static class ProTeamOpenTapped extends BookingDetailsLog
    {
        private static final String EVENT_TYPE = "pro_team_open_tapped";

        public ProTeamOpenTapped()
        {
            super(EVENT_TYPE);
        }
    }

    public enum EventType
    {
        SELECTED("selected"),
        SUBMITTED("submitted"),
        SUCCESS("success"),
        ERROR("error");

        private String value;

        EventType(final String value)
        {
            this.value = value;
        }

        String getValue()
        {
            return value;
        }
    }

}

