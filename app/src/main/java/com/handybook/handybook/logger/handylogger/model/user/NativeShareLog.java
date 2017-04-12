package com.handybook.handybook.logger.handylogger.model.user;

import android.support.annotation.StringDef;

import com.google.gson.annotations.SerializedName;
import com.handybook.handybook.logger.handylogger.model.EventLog;

import static com.handybook.handybook.logger.handylogger.model.booking.EventType.SHARE_BUTTON_TAPPED;
import static com.handybook.handybook.logger.handylogger.model.booking.EventType.SHARE_METHOD_SELECTED;

/**
 * This is used to record a share action triggered by the user.
 */
public class NativeShareLog extends EventLog {

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


    @StringDef({SHARE_BUTTON_TAPPED, SHARE_METHOD_SELECTED})
    @interface EventType {}

    public NativeShareLog(
            @EventType final String eventType,
            final String eventContext,
            final String referralMedium,
            final String referralIdentifier,
            final String couponCode,
            final String ctaSource,
            final int senderOfferAmount,
            final int receiverOfferAmount
    ) {
        super(eventType, eventContext);
        init(
                referralMedium,
                referralIdentifier,
                couponCode,
                ctaSource,
                senderOfferAmount,
                receiverOfferAmount
        );
    }

    private void init(
            final String referralMedium,
            final String referralIdentifier,
            final String couponCode,
            final String ctaSource,
            final int senderOfferAmount,
            final int receiverOfferAmount
    ) {
        mReferralMedium = referralMedium;
        mReferralIdentifier = referralIdentifier;
        mCouponCode = couponCode;
        mCtaSource = ctaSource;
        mSenderOfferAmount = senderOfferAmount;
        mReceiverOfferAmount = receiverOfferAmount;
    }

    /**
     * Log this when a specific pro is being shared, and sees a list of mediums to share the
     * pro through
     */
    public static class NativeShareProLog extends NativeShareLog {

        @SerializedName("share_type")
        private String mShareType = "pro_profile";

        @SerializedName("provider_id")
        private String mProviderId;

        public NativeShareProLog(
                @EventType final String eventType,
                final String eventContext,
                final String providerId,
                final String referralMedium,
                final String referralIdentifier,
                final String couponCode,
                final String ctaSource,
                final int senderOfferAmount,
                final int receiverOfferAmount
        ) {
            super(
                    eventType,
                    eventContext,
                    referralMedium,
                    referralIdentifier,
                    couponCode,
                    ctaSource,
                    senderOfferAmount,
                    receiverOfferAmount
            );

            mProviderId = providerId;
        }
    }


}
