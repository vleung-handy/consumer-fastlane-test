package com.handybook.handybook.logger.handylogger.model.user;

import com.google.gson.annotations.SerializedName;
import com.handybook.handybook.logger.handylogger.model.EventLog;


public class ShareModalLog extends EventLog
{
    @SerializedName("referral_medium")
    private String mReferralMedium;
    @SerializedName("referral_identifier")
    private String mReferralIdentifier;
    @SerializedName("coupon_code")
    private String mCouponCode;
    @SerializedName("sender_offer_amount")
    private int mSenderOfferAmount;
    @SerializedName("receiver_offer_amount")
    private int mReceiverOfferAmount;

    public ShareModalLog(
            final String eventType, final String eventContext, final String referralMedium,
            final String referralIdentifier, final String couponCode,
            final int senderOfferAmount, final int receiverOfferAmount
    )
    {
        super(eventType, eventContext);
        mReferralMedium = referralMedium;
        mReferralIdentifier = referralIdentifier;
        mCouponCode = couponCode;
        mSenderOfferAmount = senderOfferAmount;
        mReceiverOfferAmount = receiverOfferAmount;
    }

    public static class PostBookingShareButtonTappedLog extends ShareModalLog
    {
        public static final String EVENT_CONTEXT = "post_booking_share_modal";
        private static final String EVENT_TYPE = "share_button_tapped";

        public PostBookingShareButtonTappedLog(
                final String referralMedium,
                final String referralIdentifier,
                final String couponCode,
                final int senderOfferAmount,
                final int receiverOfferAmount
        )
        {
            super(EVENT_TYPE, EVENT_CONTEXT, referralMedium, referralIdentifier, couponCode,
                    senderOfferAmount, receiverOfferAmount);
        }
    }

    public static class PostRatingShareButtonTappedLog extends ShareModalLog
    {
        public static final String EVENT_CONTEXT = "post_rating_share_modal";
        private static final String EVENT_TYPE = "share_button_tapped";

        public PostRatingShareButtonTappedLog(
                final String referralMedium,
                final String referralIdentifier,
                final String couponCode,
                final int senderOfferAmount,
                final int receiverOfferAmount
        )
        {
            super(EVENT_TYPE, EVENT_CONTEXT, referralMedium, referralIdentifier, couponCode,
                  senderOfferAmount, receiverOfferAmount);
        }
    }
}
