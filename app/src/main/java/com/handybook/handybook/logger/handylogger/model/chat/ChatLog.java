package com.handybook.handybook.logger.handylogger.model.chat;

import android.support.annotation.Nullable;

import com.google.gson.annotations.SerializedName;
import com.handybook.handybook.logger.handylogger.model.EventLog;

import java.util.Date;

public class ChatLog extends EventLog
{
    private static final String EVENT_CONTEXT = "pro_team_conversations";

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
     * pro team conversations loaded/shown
     */
    public static class ConversationsShownLog extends ChatLog
    {
        private static final String EVENT_TYPE = "conversations_shown";

        @SerializedName("pro_team_chat_enabled_members_count")
        private final int mProTeamChatEnabledMembersCount;

        @SerializedName("total_conversations_count")
        private final int mTotalConversationsCount;

        public ConversationsShownLog(
                final int proTeamChatEnabledMembersCount,
                final int totalConversationsCount
        )
        {
            super(EVENT_TYPE);
            mProTeamChatEnabledMembersCount = proTeamChatEnabledMembersCount;
            mTotalConversationsCount = totalConversationsCount;
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

        @SerializedName("recurring_id")
        @Nullable
        private final String mRecurringId;

        public RescheduleBookingSelectedLog(
                final String providerId,
                final String bookingId,
                @Nullable final String recurringId
        )
        {
            super(EVENT_TYPE);
            mProviderId = providerId;
            mBookingId = bookingId;
            mRecurringId = recurringId;
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

        @SerializedName("recurring_id")
        @Nullable
        private final String mRecurringId;

        public RescheduleSubmittedLog(
                final String providerId,
                final String bookingId,
                final Date oldDate,
                final Date newDate,
                @Nullable final String recurringId
        )
        {
            super(EVENT_TYPE);
            mProviderId = providerId;
            mBookingId = bookingId;
            mOldDate = oldDate;
            mNewDate = newDate;
            mRecurringId = recurringId;
        }
    }

//    TODO: JIA: refactor all these reschedule logs to have some sort of a base with all those properties


    /**
     * user successfully reschedules
     */
    public static class RescheduleSuccessLog extends ChatLog
    {
        private static final String EVENT_TYPE = "reschedule_success";

        @SerializedName("provider_id")
        private String mProviderId;

        @SerializedName("booking_id")
        private String mBookingId;

        @SerializedName("old_date")
        private final Date mOldDate;

        @SerializedName("new_date")
        private final Date mNewDate;

        @SerializedName("recurring_id")
        @Nullable
        private final String mRecurringId;

        public RescheduleSuccessLog(
                final String providerId,
                final String bookingId,
                final Date oldDate,
                final Date newDate,
                @Nullable final String recurringId
        )
        {
            super(EVENT_TYPE);
            mProviderId = providerId;
            mBookingId = bookingId;
            mOldDate = oldDate;
            mNewDate = newDate;
            mRecurringId = recurringId;
        }
    }


    /**
     * user receives an error while rescheduling
     */
    public static class RescheduleErrorLog extends ChatLog
    {
        private static final String EVENT_TYPE = "reschedule_error";

        @SerializedName("provider_id")
        private String mProviderId;

        @SerializedName("booking_id")
        private String mBookingId;

        @SerializedName("old_date")
        private final Date mOldDate;

        @SerializedName("new_date")
        private final Date mNewDate;

        @SerializedName("recurring_id")
        @Nullable
        private final String mRecurringId;

        public RescheduleErrorLog(
                final String providerId,
                final String bookingId,
                final Date oldDate,
                final Date newDate,
                @Nullable final String recurringId
        )
        {
            super(EVENT_TYPE);
            mProviderId = providerId;
            mBookingId = bookingId;
            mOldDate = oldDate;
            mNewDate = newDate;
            mRecurringId = recurringId;
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


    public static class PushNotificationReceived extends ChatLog
    {
        private static final String EVENT_TYPE = "push_notification_received";

        public PushNotificationReceived()
        {
            super(EVENT_TYPE);
        }
    }
}
