package com.handybook.handybook.logger.handylogger.model.user;

import com.google.gson.annotations.SerializedName;
import com.handybook.handybook.logger.handylogger.model.EventLog;

public class ShareModalLog extends EventLog {

    public static final String SRC_UPCOMING_BOOKINGS = "upcoming_bookings";
    public static final String SRC_PAST_BOOKINGS = "past_bookings";

    private static final String EVENT_TYPE = "share_button_tapped";

    @SerializedName("referral_medium")
    private String mReferralMedium;
    @SerializedName("referral_identifier")
    private String mReferralIdentifier;
    @SerializedName("coupon_code")
    private String mCouponCode;
    @SerializedName("cta_source")
    private String mCtaSource;
    @SerializedName("sender_offer_amount")
    private int mSenderOfferAmount;
    @SerializedName("receiver_offer_amount")
    private int mReceiverOfferAmount;

    public ShareModalLog(
            final String eventContext,
            final String referralMedium,
            final String referralIdentifier,
            final String couponCode,
            final String ctaSource,
            final int senderOfferAmount,
            final int receiverOfferAmount
    ) {
        super(EVENT_TYPE, eventContext);
        mReferralMedium = referralMedium;
        mReferralIdentifier = referralIdentifier;
        mCouponCode = couponCode;
        mCtaSource = ctaSource;
        mSenderOfferAmount = senderOfferAmount;
        mReceiverOfferAmount = receiverOfferAmount;
    }

    public static class PostBookingShareButtonTappedLog extends ShareModalLog {

        public static final String EVENT_CONTEXT = "post_booking_share_modal";

        public PostBookingShareButtonTappedLog(
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


    public static class PostRatingShareButtonTappedLog extends ShareModalLog {

        public static final String EVENT_CONTEXT = "post_rating_share_modal";

        public PostRatingShareButtonTappedLog(
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


    public static class NativeShareTappedLog extends ShareModalLog {

        private static final String EVENT_CONTEXT = "native_share";

        public NativeShareTappedLog(
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
}
