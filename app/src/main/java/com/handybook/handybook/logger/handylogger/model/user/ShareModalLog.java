package com.handybook.handybook.logger.handylogger.model.user;

import com.google.gson.annotations.SerializedName;
import com.handybook.handybook.logger.handylogger.model.EventLog;


public class ShareModalLog extends EventLog
{
    public static final String EVENT_CONTEXT_POST_RATING = "post_rating_share_modal";
    public static final String EVENT_CONTEXT_POST_BOOKING = "post_booking_share_modal";

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

    public static class ShareButtonTappedLog extends ShareModalLog
    {
        private static final String EVENT_TYPE = "share_button_tapped";

        public ShareButtonTappedLog(
                final String eventContext,
                final String referralMedium,
                final String referralIdentifier,
                final String couponCode,
                final int senderOfferAmount,
                final int receiverOfferAmount
        )
        {
            super(EVENT_TYPE, eventContext, referralMedium, referralIdentifier, couponCode,
                    senderOfferAmount, receiverOfferAmount);
        }
    }
}
