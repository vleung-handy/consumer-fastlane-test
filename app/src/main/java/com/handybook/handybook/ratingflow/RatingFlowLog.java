package com.handybook.handybook.ratingflow;

import android.support.annotation.NonNull;

import com.google.gson.annotations.SerializedName;
import com.handybook.handybook.booking.model.Provider;
import com.handybook.handybook.logger.handylogger.model.EventLog;
import com.handybook.handybook.logger.handylogger.model.user.NativeShareLog;
import com.handybook.handybook.logger.handylogger.model.user.ShareModalLog;

import java.util.ArrayList;
import java.util.List;

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


    public static class RecommendedProvidersShown extends RatingFlowLog {

        @SerializedName("provider_ids")
        private ArrayList<String> mProviderIds;
        @SerializedName("num_pros")
        private int mNumberOfProviders;
        @SerializedName("user_id")
        private String mUserId;
        @SerializedName("booking_id")
        private String mBookingId;

        public static final String EVENT_TYPE = "recommended_pros_shown";

        public RecommendedProvidersShown(
                @NonNull final List<Provider> recommendedProviders,
                final String userId,
                final String bookingId
        ) {
            super(EVENT_TYPE);
            mProviderIds = new ArrayList<>();
            for (final Provider recommendedProvider : recommendedProviders) {
                mProviderIds.add(recommendedProvider.getId());
            }
            mNumberOfProviders = recommendedProviders.size();
            mUserId = userId;
            mBookingId = bookingId;
        }
    }


    public static class ProviderAdded extends RatingFlowLog {

        public static final String EVENT_TYPE = "provider_added";

        @SerializedName("user_id")
        private String mUserId;
        @SerializedName("provider_id")
        private String mProviderId;

        public ProviderAdded(final String userId, final String providerId) {
            super(EVENT_TYPE);
            mUserId = userId;
            mProviderId = providerId;
        }
    }


    public static class ReferralPageShown extends RatingFlowLog {

        public static final String EVENT_TYPE = "referral_page_shown";

        public ReferralPageShown() {
            super(EVENT_TYPE);
        }
    }


    public static class ShareButtonTapped extends ShareModalLog {

        public ShareButtonTapped(
                final String referralMedium,
                final String referralIdentifier,
                final String couponCode,
                final String ctaSource,
                final int senderOfferAmount,
                final int receiverOfferAmount
        ) {
            super(
                    EVENT_CONTEXT,
                    referralMedium,
                    referralIdentifier,
                    couponCode,
                    ctaSource,
                    senderOfferAmount,
                    receiverOfferAmount
            );
        }
    }


    public static class ShareMethodSelected extends NativeShareLog {

        public static final String EVENT_TYPE = "share_method_selected";

        public ShareMethodSelected(
                final String referralMedium,
                final String referralIdentifier,
                final String couponCode,
                final String ctaSource,
                final int senderOfferAmount,
                final int receiverOfferAmount
        ) {
            super(
                    EVENT_TYPE,
                    EVENT_CONTEXT,
                    referralMedium,
                    referralIdentifier,
                    couponCode,
                    ctaSource,
                    senderOfferAmount,
                    receiverOfferAmount
            );
        }
    }

}
