package com.handybook.handybook.logger.handylogger.model.user;


import com.google.gson.annotations.SerializedName;
import com.handybook.handybook.logger.handylogger.model.EventLog;

public class NativeShareLog extends EventLog
{
    private static final String EVENT_CONTEXT = "native_share";

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

    public NativeShareLog(
            final String eventType, final String referralMedium,
            final String referralIdentifier, final String couponCode,
            final int senderOfferAmount, final int receiverOfferAmount
    )
    {
        super(eventType, EVENT_CONTEXT);
        mReferralMedium = referralMedium;
        mReferralIdentifier = referralIdentifier;
        mCouponCode = couponCode;
        mSenderOfferAmount = senderOfferAmount;
        mReceiverOfferAmount = receiverOfferAmount;
    }

    public static class NativeShareButtonTapped extends NativeShareLog
    {
        private static final String EVENT_TYPE = "share_button_tapped";


        public NativeShareButtonTapped(
                final String referralMedium, final String referralIdentifier,
                final String couponCode, final int senderOfferAmount,
                final int receiverOfferAmount
        )
        {
            super(EVENT_TYPE, referralMedium, referralIdentifier, couponCode,
                    senderOfferAmount, receiverOfferAmount);
        }
    }
}
