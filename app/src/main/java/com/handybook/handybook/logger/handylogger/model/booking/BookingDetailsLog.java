package com.handybook.handybook.logger.handylogger.model.booking;

import android.support.annotation.Nullable;

import com.google.gson.annotations.SerializedName;
import com.handybook.handybook.booking.model.Provider;
import com.handybook.handybook.booking.ui.fragment.BookingDetailFragment;
import com.handybook.handybook.logger.handylogger.model.EventLog;

import java.util.Date;

public abstract class BookingDetailsLog extends EventLog {

    private static final String EVENT_CONTEXT = "booking_details";

    private BookingDetailsLog(final String eventType) {
        super(eventType, EVENT_CONTEXT);
    }

    public static class BookingDetailsShownLog extends BookingDetailsLog {

        private static final String EVENT_TYPE = "shown";

        public BookingDetailsShownLog() {
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
    public static class EntryMethodLog extends BookingDetailsLog {

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
        ) {
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
        public static class RecommendationShown extends EntryMethodLog {

            private static final String EVENT_TYPE_SUFFIX = "recommendation_shown";

            public RecommendationShown(
                    final String bookingId,
                    final boolean isBookingRecurring,
                    final String entryMethodMachineName
            ) {
                super(EVENT_TYPE_SUFFIX, bookingId, isBookingRecurring, entryMethodMachineName);
            }
        }


        /*
        in logging terms, entry method info is "submitted"
        when the user clicks next, not when the network call itself is made
        */
        public static class InfoSubmitted extends EntryMethodLog {

            private static final String EVENT_TYPE_SUFFIX = "info_submitted";

            public InfoSubmitted(
                    final String bookingId,
                    final boolean isBookingRecurring,
                    final String entryMethodMachineName
            ) {
                super(EVENT_TYPE_SUFFIX, bookingId, isBookingRecurring, entryMethodMachineName);
            }
        }
    }


    public static class RescheduleDatePickerShownLog extends BookingFunnelLog {

        private static final String EVENT_TYPE = "reschedule_date_picker_shown";

        @SerializedName("provider_id")
        @Nullable
        private final String mProviderId;

        @SerializedName("booking_id")
        private final String mBookingId;

        @SerializedName("original_date_start")
        private final Date mOriginalStartDate;

        @SerializedName("source")
        private final String mSource;

        public RescheduleDatePickerShownLog(String providerId, String bookingId, Date startDate, BookingDetailFragment.RescheduleType rescheduleType) {
            super(EVENT_TYPE);
            mProviderId = providerId;
            mBookingId = bookingId;
            mOriginalStartDate = startDate;
            mSource = rescheduleType.getSourceName();
        }
    }

    private static class RescheduleBaseLog extends BookingDetailsLog {

        private static final String EVENT_TYPE_PREFIX = "reschedule_";

        @SerializedName("booking_id")
        private final String mBookingId;

        @SerializedName("old_date")
        private final Date mOldDate;

        @SerializedName("new_date")
        private final Date mNewDate;

        @SerializedName("provider_id")
        @Nullable
        private final String mProviderId;

        @SerializedName("recurring_id")
        @Nullable
        private final String mRecurringId;

        public RescheduleBaseLog(
                String eventTypeSuffix,
                @Nullable final String providerId,
                final String bookingId,
                @Nullable final Long recurringId,
                final Date oldDate,
                final Date newDate
        ) {
            this(eventTypeSuffix, providerId, bookingId, recurringId == null ? null : recurringId.toString(), oldDate, newDate);
        }

        public RescheduleBaseLog(
                String eventTypeSuffix,
                @Nullable final String providerId,
                final String bookingId,
                @Nullable final String recurringId,
                final Date oldDate,
                final Date newDate
        ) {
            super(EVENT_TYPE_PREFIX + eventTypeSuffix);
            mProviderId = providerId;
            mBookingId = bookingId;
            mRecurringId = recurringId;
            mOldDate = oldDate;
            mNewDate = newDate;
        }
    }


    /**
     * user selects a booking to reschedule
     */
    public static class RescheduleBookingSelectedLog extends RescheduleBaseLog {

        private static final String EVENT_TYPE_SUFFIX = "booking_selected";

        public RescheduleBookingSelectedLog(
                @Nullable final Provider provider,
                final String bookingId,
                @Nullable final Long recurringId
        ) {
            super(EVENT_TYPE_SUFFIX, provider == null ? null : provider.getId(), bookingId, recurringId, null, null);
        }
    }


    /**
     * user submits a reschedule
     */
    public static class RescheduleSubmittedLog extends RescheduleBaseLog {

        private static final String EVENT_TYPE_SUFFIX = "submitted";

        @SerializedName("source")
        private final String mSource;

        @SerializedName("is_instant")
        private final boolean mIsInstantBookEnabled;

        public RescheduleSubmittedLog(
                @Nullable final String providerId,
                final String bookingId,
                final Date oldDate,
                final Date newDate,
                @Nullable final String recurringId,
                final BookingDetailFragment.RescheduleType rescheduleType,
                final boolean isInstantBookEnabled
        ) {
            super(EVENT_TYPE_SUFFIX, providerId, bookingId, recurringId, oldDate, newDate);
            mSource = rescheduleType.getSourceName();
            mIsInstantBookEnabled = isInstantBookEnabled;
        }
    }


    /**
     * user successfully reschedules
     */
    public static class RescheduleSuccessLog extends RescheduleBaseLog {

        private static final String EVENT_TYPE_SUFFIX = "success";

        @SerializedName("source")
        private final String mSource;

        @SerializedName("is_instant")
        private final boolean mIsInstantBookEnabled;

        public RescheduleSuccessLog(
                @Nullable final String provider,
                final String bookingId,
                final Date oldDate,
                final Date newDate,
                @Nullable final String recurringId,
                final BookingDetailFragment.RescheduleType rescheduleType,
                final boolean isInstantBookEnabled
                ) {
            super(EVENT_TYPE_SUFFIX, provider, bookingId, recurringId, oldDate, newDate);
            mSource = rescheduleType.getSourceName();
            mIsInstantBookEnabled = isInstantBookEnabled;
        }
    }


    /**
     * user receives an error while rescheduling
     */
    public static class RescheduleErrorLog extends RescheduleBaseLog {

        private static final String EVENT_TYPE_SUFFIX = "error";

        public RescheduleErrorLog(
                @Nullable final String providerId,
                final String bookingId,
                final Date oldDate,
                final Date newDate,
                @Nullable final String recurringId
        ) {
            super(EVENT_TYPE_SUFFIX, providerId, bookingId, recurringId, oldDate, newDate);
        }
    }


    public static class CancelBooking extends BookingDetailsLog {
        private static final String EVENT_TYPE = "booking_cancelled";
        @SerializedName("booking_id")
        private final String mBookingId;

        @SerializedName("booking_cancellation_reason")
        private final String mBookingCancellationReason;

        public CancelBooking(String bookingId, String bookingCancellationReason) {
            super(EVENT_TYPE);
            mBookingId = bookingId;
            mBookingCancellationReason = bookingCancellationReason;
        }
    }


    public static class ContinueSkipSelected extends BookingDetailsLog {

        private static final String EVENT_TYPE = "continue_skip_selected";

        @SerializedName("booking_id")
        private final String mBookingId;

        public ContinueSkipSelected(final String bookingId) {
            super(EVENT_TYPE);
            mBookingId = bookingId;
        }
    }


    public static class ProTeamOpenTapped extends BookingDetailsLog {

        private static final String EVENT_TYPE = "pro_team_open_tapped";

        public ProTeamOpenTapped() {
            super(EVENT_TYPE);
        }
    }


    public static class RescheduleSelectedProShown extends BookingDetailsLog {

        private static final String EVENT_TYPE = "reschedule_select_pro_shown";

        @SerializedName("num_providers")
        private final int mNumProviders;

        public RescheduleSelectedProShown(final int numProviders) {
            super(EVENT_TYPE);
            mNumProviders = numProviders;
        }
    }


    public static class RescheduleIndifferenceSelected extends BookingDetailsLog {

        private static final String EVENT_TYPE = "reschedule_indifferent_selected";

        public RescheduleIndifferenceSelected() {
            super(EVENT_TYPE);
        }
    }
}

