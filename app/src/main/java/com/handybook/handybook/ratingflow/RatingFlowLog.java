package com.handybook.handybook.ratingflow;

import com.google.gson.annotations.SerializedName;
import com.handybook.handybook.logger.handylogger.model.EventLog;

public class RatingFlowLog extends EventLog {

    private static final String EVENT_CONTEXT = "rating_flow";

    private RatingFlowLog(final String eventType) {
        super(eventType, EVENT_CONTEXT);
    }

    private static class RatingLog extends RatingFlowLog {

        @SerializedName("rating")
        private int mRating;
        @SerializedName("tip_amount")
        private int mTipAmount;
        @SerializedName("booking_id")
        private int mBookingId;
        @SerializedName("provider_id")
        private int mProviderId;

        private RatingLog(
                final String eventType,
                final int rating,
                final int tipAmount,
                final int bookingId,
                final int providerId
        ) {
            super(eventType);
            mRating = rating;
            mTipAmount = tipAmount;
            mBookingId = bookingId;
            mProviderId = providerId;
        }
    }


    public static class RatingSubmitted extends RatingLog {

        private static final String EVENT_TYPE = "rating_submitted";

        public RatingSubmitted(
                final int rating,
                final int tipAmount,
                final int bookingId,
                final int providerId
        ) {
            super(EVENT_TYPE, rating, tipAmount, bookingId, providerId);
        }
    }


    public static class RatingSuccess extends RatingLog {

        private static final String EVENT_TYPE = "rating_success";

        public RatingSuccess(
                final int rating,
                final int tipAmount,
                final int bookingId,
                final int providerId
        ) {
            super(EVENT_TYPE, rating, tipAmount, bookingId, providerId);
        }
    }


    public static class RatingError extends RatingLog {

        private static final String EVENT_TYPE = "rating_error";

        public RatingError(
                final int rating,
                final int tipAmount,
                final int bookingId,
                final int providerId
        ) {
            super(EVENT_TYPE, rating, tipAmount, bookingId, providerId);
        }
    }


    public static class ProPreferenceSubmitted extends RatingFlowLog {

        private static final String EVENT_TYPE = "pro_preference_submitted";

        @SerializedName("keep_working")
        private boolean mOptedIn;
        @SerializedName("booking_id")
        private int mBookingId;
        @SerializedName("provider_id")
        private int mProviderId;

        public ProPreferenceSubmitted(
                final boolean optedIn,
                final int bookingId,
                final int providerId
        ) {
            super(EVENT_TYPE);
            mOptedIn = optedIn;
            mBookingId = bookingId;
            mProviderId = providerId;
        }
    }


    public static class FeedbackSubmitted extends RatingFlowLog {

        private static final String EVENT_TYPE = "pro_preference_submitted";

        @SerializedName("page")
        private String mPage;
        @SerializedName("booking_id")
        private int mBookingId;

        public FeedbackSubmitted(final String page, final int bookingId) {
            super(EVENT_TYPE);
            mPage = page;
            mBookingId = bookingId;
        }
    }


    public static class ConfirmationSubmitted extends RatingFlowLog {

        private static final String EVENT_TYPE = "confirmation_submitted";

        @SerializedName("booking_id")
        private int mBookingId;
        @SerializedName("provider_id")
        private int mProviderId;

        public ConfirmationSubmitted(
                final int bookingId,
                final int providerId
        ) {
            super(EVENT_TYPE);
            mBookingId = bookingId;
            mProviderId = providerId;
        }
    }
}
