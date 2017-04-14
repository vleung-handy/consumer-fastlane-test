package com.handybook.handybook.ratingflow;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringDef;

import com.google.gson.annotations.SerializedName;
import com.handybook.handybook.booking.model.Provider;
import com.handybook.handybook.logger.handylogger.constants.EventType;
import com.handybook.handybook.logger.handylogger.model.EventLog;
import com.handybook.handybook.logger.handylogger.model.user.NativeShareLog;

import java.util.ArrayList;
import java.util.List;

import static com.handybook.handybook.logger.handylogger.constants.EventType.EVENT_TYPE_SHOWN;
import static com.handybook.handybook.logger.handylogger.constants.EventType.EVENT_TYPE_SKIPPED;
import static com.handybook.handybook.logger.handylogger.constants.EventType.EVENT_TYPE_SUBMITTED;

public class RatingFlowLog extends EventLog {

    private static final String EVENT_CONTEXT = "rating_flow";

    private RatingFlowLog(final String eventType) {
        super(eventType, EVENT_CONTEXT);
    }

    @StringDef({EVENT_TYPE_SHOWN, EVENT_TYPE_SUBMITTED, EVENT_TYPE_SKIPPED})
    @interface EventTypePostfix {}

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


    public static class ProPreferenceLog extends RatingFlowLog {

        private static final String EVENT_TYPE = "pro_preference_page_";

        @SerializedName("keep_working")
        private boolean mOptedIn;
        @SerializedName("booking_id")
        private int mBookingId;
        @SerializedName("provider_id")
        private int mProviderId;

        public ProPreferenceLog(
                @EventTypePostfix final String eventTypePostfix,
                final boolean optedIn,
                final int bookingId,
                final int providerId
        ) {
            super(EVENT_TYPE + eventTypePostfix);
            mOptedIn = optedIn;
            mBookingId = bookingId;
            mProviderId = providerId;
        }
    }


    public static class LowRatingReasonsLog extends RatingFlowLog {

        private static final String EVENT_TYPE = "low_rating_reason_page_";

        @SerializedName("page")
        private String mPage;
        @SerializedName("booking_id")
        private int mBookingId;

        public LowRatingReasonsLog(
                @EventTypePostfix final String eventTypePostfix,
                final String page,
                final int bookingId
        ) {
            super(EVENT_TYPE + eventTypePostfix);
            mPage = page;
            mBookingId = bookingId;
        }
    }


    public static class ProfileReviewLog extends RatingFlowLog {

        private static final String EVENT_TYPE = "profile_review_page_";

        @SerializedName("provider_id")
        private Integer mProviderId;

        public ProfileReviewLog(
                @EventTypePostfix final String eventTypePostfix,
                final Integer providerId
        ) {
            super(EVENT_TYPE + eventTypePostfix);
            mProviderId = providerId;
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


    public static class RatingAndTipLog extends RatingFlowLog {

        public static final String EVENT_TYPE = "rating_and_tip_page_";

        @SerializedName("provider_id")
        private Integer mProviderId;

        public RatingAndTipLog(
                @EventTypePostfix final String eventTypePostfix,
                @Nullable final Integer providerId
        ) {
            super(EVENT_TYPE + eventTypePostfix);
            mProviderId = providerId;
        }
    }


    public static class ReferralPageLog extends RatingFlowLog {

        public static final String EVENT_TYPE = "referral_page_";

        @SerializedName("provider_ids")
        private List<Integer> mProviderIds;

        public ReferralPageLog(
                @EventTypePostfix final String eventTypePostfix,
                @Nullable final List<Integer> providerIds
        ) {
            super(EVENT_TYPE + eventTypePostfix);

            mProviderIds = providerIds;
        }
    }


    public static class CxFeedbackLog extends RatingFlowLog {

        public static final String EVENT_TYPE = "cx_feedback_page_";

        public CxFeedbackLog(@EventTypePostfix final String eventTypePostfix) {
            super(EVENT_TYPE + eventTypePostfix);
        }
    }


    public static class ShareButtonForProTapped extends NativeShareLog.NativeShareProLog {
        public ShareButtonForProTapped(
                final String providerId,
                final String referralMedium,
                final String referralIdentifier,
                final String couponCode,
                final String ctaSource,
                final int senderOfferAmount,
                final int receiverOfferAmount
        ) {
            super(
                    EventType.SHARE_BUTTON_TAPPED,
                    EVENT_CONTEXT,
                    providerId,
                    referralMedium,
                    referralIdentifier,
                    couponCode,
                    ctaSource,
                    senderOfferAmount,
                    receiverOfferAmount
            );
        }
    }


    public static class ShareMethodForProSelected extends NativeShareLog.NativeShareProLog {

        public ShareMethodForProSelected(
                final String providerId,
                final String referralMedium,
                final String referralIdentifier,
                final String couponCode,
                final String ctaSource,
                final int senderOfferAmount,
                final int receiverOfferAmount
        ) {
            super(
                    EventType.SHARE_METHOD_SELECTED,
                    EVENT_CONTEXT,
                    providerId,
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

        public ShareMethodSelected(
                final String referralMedium,
                final String referralIdentifier,
                final String couponCode,
                final String ctaSource,
                final int senderOfferAmount,
                final int receiverOfferAmount
        ) {
            super(
                    EventType.SHARE_METHOD_SELECTED,
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
