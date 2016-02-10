package com.handybook.handybook.module.referral.model;

import com.google.gson.annotations.SerializedName;

public class ReferralResponse
{
    @SerializedName("referrals")
    private ReferralDescriptor mReferralDescriptor;

    public ReferralDescriptor getReferralDescriptor()
    {
        return mReferralDescriptor;
    }
}
