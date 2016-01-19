package com.handybook.handybook.module.referral.model;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringDef;

import com.google.gson.annotations.SerializedName;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class ReferralDescriptor
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
    private ReferralMedia mReferralPageMedia;
    @SerializedName(SOURCE_PROFILE_PAGE)
    private ReferralMedia mProfilePageMedia;
    @SerializedName(SOURCE_CONFIRMATION_PAGE)
    private ReferralMedia mConfirmationPageMedia;
    @SerializedName(SOURCE_HIGH_RATING_MODAL)
    private ReferralMedia mHighRatingModalMedia;

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

    @Nullable
    public ReferralMedia getReferralMedia(@NonNull @Source final String source)
    {
        switch (source)
        {
            case SOURCE_REFERRAL_PAGE:
                return mReferralPageMedia;
            case SOURCE_PROFILE_PAGE:
                return mProfilePageMedia;
            case SOURCE_CONFIRMATION_PAGE:
                return mConfirmationPageMedia;
            case SOURCE_HIGH_RATING_MODAL:
                return mHighRatingModalMedia;
            default:
                return null;
        }
    }
}
