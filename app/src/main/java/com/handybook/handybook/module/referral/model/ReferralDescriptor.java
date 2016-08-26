package com.handybook.handybook.module.referral.model;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringDef;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Holds ReferralChannels objects for each supported source where in-app sharing could exist which
 * includes the referral page and the profile page. It also holds information about the user
 * referral which are not specific to the source (e.g. sender credit, receiver credit and coupon
 * code).
 */
public class ReferralDescriptor implements Serializable
{
    public static final String SOURCE_REFERRAL_PAGE = "referral_page";
    public static final String SOURCE_PROFILE_PAGE = "profile_page";
    public static final String SOURCE_CONFIRMATION_PAGE = "confirmation_page";
    public static final String SOURCE_HIGH_RATING_MODAL = "high_rating_modal";

    @SerializedName("sender_credit_amount")
    private int mSenderCreditAmount;
    @SerializedName("receiver_coupon_amount")
    private int mReceiverCouponAmount;
    @SerializedName("coupon_code")
    private String mCouponCode;
    @SerializedName(SOURCE_REFERRAL_PAGE)
    private ReferralChannels mReferralPageChannels;
    @SerializedName(SOURCE_PROFILE_PAGE)
    private ReferralChannels mProfilePageChannels;
    @SerializedName(SOURCE_CONFIRMATION_PAGE)
    private ReferralChannels mConfirmationPageChannels;
    @SerializedName(SOURCE_HIGH_RATING_MODAL)
    private ReferralChannels mHighRatingModalChannels;

    public int getSenderCreditAmount()
    {
        return mSenderCreditAmount;
    }

    public int getReceiverCouponAmount()
    {
        return mReceiverCouponAmount;
    }

    public String getCouponCode()
    {
        return mCouponCode;
    }

    @Retention(RetentionPolicy.SOURCE)
    @StringDef({
            SOURCE_REFERRAL_PAGE,
            SOURCE_PROFILE_PAGE,
            SOURCE_CONFIRMATION_PAGE,
            SOURCE_HIGH_RATING_MODAL
    })
    public @interface Source
    {
    }

    /**
     * Returns ReferralChannels object associated with the supported source provided.
     *
     * @param source a supported source
     * @return the object associated with the source provided
     * @see ReferralChannels
     * @see com.handybook.handybook.module.referral.model.ReferralDescriptor.Source
     */
    @Nullable
    public ReferralChannels getReferralChannelsForSource(@NonNull @Source final String source)
    {
        switch (source)
        {
            case SOURCE_REFERRAL_PAGE:
                return mReferralPageChannels;
            case SOURCE_PROFILE_PAGE:
                return mProfilePageChannels;
            case SOURCE_CONFIRMATION_PAGE:
                return mConfirmationPageChannels;
            case SOURCE_HIGH_RATING_MODAL:
                return mHighRatingModalChannels;
            default:
                return null;
        }
    }
}
