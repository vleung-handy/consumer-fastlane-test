package com.handybook.handybook.logger.handylogger.model.chat;

import com.google.gson.annotations.SerializedName;
import com.handybook.handybook.logger.handylogger.model.EventLog;

import java.util.Date;

public class ChatLog extends EventLog
{

    //    TODO: JIA: Do a final check against the logging specs, since it's been changing.
    private static final String EVENT_CONTEXT = "chat";

    public ChatLog(final String eventType)
    {
        super(eventType, EVENT_CONTEXT);
    }

    /**
     * user selects a chat conversation:
     */
    public static class ConversationSelectedLog extends ChatLog
    {
        private static final String EVENT_TYPE = "conversation_selected";

        @SerializedName("provider_id")
        private final String mProviderId;


        @SerializedName("layer_conversation_id")
        private final String mConversationId;

        public ConversationSelectedLog(
                final String providerId,
                final String conversationId
        )
        {
            super(EVENT_TYPE);
            mProviderId = providerId;
            mConversationId = conversationId;
        }
    }


    /**
     * New conversation is created:
     */
    public static class ConversationCreatedLog extends ChatLog
    {
        private static final String EVENT_TYPE = "conversation_created";

        @SerializedName("provider_id")
        private final String mProviderId;

        @SerializedName("layer_conversation_id")
        private final String mConversationId;

        public ConversationCreatedLog(
                final String providerId,
                final String conversationId
        )
        {
            super(EVENT_TYPE);
            mProviderId = providerId;
            mConversationId = conversationId;
        }
    }


    /**
     * user selects button to reschedule booking
     */
    public static class RescheduleSelectedLog extends ChatLog
    {
        private static final String EVENT_TYPE = "reschedule_selected";

        @SerializedName("provider_id")
        private final String mProviderId;

        @SerializedName("layer_conversation_id")
        private final String mConversationId;

        public RescheduleSelectedLog(
                final String providerId,
                final String conversationId
        )
        {
            super(EVENT_TYPE);
            mProviderId = providerId;
            mConversationId = conversationId;
        }
    }


    /**
     * user selects button to make a new booking
     */
    public static class MakeBookingSelectedLog extends ChatLog
    {
        private static final String EVENT_TYPE = "make_booking_selected";

        @SerializedName("provider_id")
        private final String mProviderId;

        @SerializedName("layer_conversation_id")
        private final String mConversationId;

        @SerializedName("service_id")
        private final String mServiceId;

        public MakeBookingSelectedLog(
                final String providerId,
                final String conversationId,
                final String serviceId
        )
        {
            super(EVENT_TYPE);
            mProviderId = providerId;
            mConversationId = conversationId;
            mServiceId = serviceId;
        }
    }


    /**
     * user selects a booking to reschedule
     */
    public static class RescheduleBookingSelectedLog extends ChatLog
    {
        private static final String EVENT_TYPE = "reschedule_booking_selected";

        @SerializedName("provider_id")
        private final String mProviderId;


        @SerializedName("booking_id")
        private final String mBookingId;

        public RescheduleBookingSelectedLog(
                final String providerId,
                final String bookingId
        )
        {
            super(EVENT_TYPE);
            mProviderId = providerId;
            mBookingId = bookingId;
        }
    }


    /**
     * user submits a reschedule
     */
    public static class RescheduleSubmittedLog extends ChatLog
    {
        private static final String EVENT_TYPE = "reschedule_submitted";

        @SerializedName("provider_id")
        private String mProviderId;

        @SerializedName("booking_id")
        private String mBookingId;

        @SerializedName("old_date")
        private final Date mOldDate;

        @SerializedName("new_date")
        private final Date mNewDate;

        public RescheduleSubmittedLog(
                final String providerId,
                final String bookingId,
                final Date oldDate,
                final Date newDate
        )
        {
            super(EVENT_TYPE);
            mProviderId = providerId;
            mBookingId = bookingId;
            mOldDate = oldDate;
            mNewDate = newDate;
        }
    }


    /**
     * error: no upcoming bookings to reschedule
     */
    public static class NoUpcomingBookingsLog extends ChatLog
    {
        private static final String EVENT_TYPE = "no_upcoming_bookings_error";

        public NoUpcomingBookingsLog()
        {
            super(EVENT_TYPE);
        }
    }

}
