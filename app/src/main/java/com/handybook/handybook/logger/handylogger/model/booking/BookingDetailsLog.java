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


    /*
    don't like that this log nearly duped in the BookingFunnelLog class file
    since the only thing that is different is the base event context

    but putting this in each file for consistency to avoid confusion
    as each logging class file currently represents a single context
    and contains only logs that are within that context
     */
    public static class EntryMethodLog extends BookingDetailsLog
    {
        private static final String EVENT_TYPE_PREFIX = "entry_method_";

        @SerializedName("booking_id")
        private final String mBookingId;

        @SerializedName("is_recurring")
        private final boolean mIsBookingRecurring;

        @SerializedName("entry_method")
        private final String mEntryMethodMachineName;

        private EntryMethodLog(
                final String eventTypeSuffix,
                final String bookingId,
                final boolean isBookingRecurring,
                final String entryMethodMachineName
        )
        {
            super(EVENT_TYPE_PREFIX + eventTypeSuffix);
            mBookingId = bookingId;
            mIsBookingRecurring = isBookingRecurring;
            mEntryMethodMachineName = entryMethodMachineName;
        }

        /*
        in logging terms, whether an entry method is "recommended"
        is whether the entry method option subtitle is present
        ex. "Chosen by 13 of your neighbors"
        */
        public static class RecommendationShown extends EntryMethodLog
        {
            private static final String EVENT_TYPE_SUFFIX = "recommendation_shown";

            public RecommendationShown(
                    final String bookingId,
                    final boolean isBookingRecurring,
                    final String entryMethodMachineName
            )
            {
                super(EVENT_TYPE_SUFFIX, bookingId, isBookingRecurring, entryMethodMachineName);
            }
        }


        /*
        in logging terms, entry method info is "submitted"
        when the user clicks next, not when the network call itself is made
        */
        public static class InfoSubmitted extends EntryMethodLog
        {
            private static final String EVENT_TYPE_SUFFIX = "info_submitted";

            public InfoSubmitted(
                    final String bookingId,
                    final boolean isBookingRecurring,
                    final String entryMethodMachineName
            )
            {
                super(EVENT_TYPE_SUFFIX, bookingId, isBookingRecurring, entryMethodMachineName);
            }
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


    public static class RescheduleSelectedProShown extends BookingDetailsLog
    {
        private static final String EVENT_TYPE = "reschedule_select_pro_shown";

        @SerializedName("num_providers")
        private final int mNumProviders;

        public RescheduleSelectedProShown(final int numProviders)
        {
            super(EVENT_TYPE);
            mNumProviders = numProviders;
        }
    }


    public static class RescheduleProviderSelected extends BookingDetailsLog
    {
        public static final String EVENT_TYPE = "reschedule_provider_selected";

        @SerializedName("provider_id")
        private final String mProviderId;
        @SerializedName("layer_conversation_id")
        private final String mConversationId;

        public RescheduleProviderSelected(final String providerId, final String conversationId)
        {
            super(EVENT_TYPE);
            mProviderId = providerId;
            mConversationId = conversationId;
        }
    }


    public static class RescheduleIndifferenceSelected extends BookingDetailsLog
    {
        private static final String EVENT_TYPE = "reschedule_indifferent_selected";

        public RescheduleIndifferenceSelected()
        {
            super(EVENT_TYPE);
        }
    }


    public static class ConversationCreatedLog extends BookingDetailsLog
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

