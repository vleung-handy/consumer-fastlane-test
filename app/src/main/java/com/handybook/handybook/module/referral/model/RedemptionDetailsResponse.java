package com.handybook.handybook.module.referral.model;

import com.google.gson.annotations.SerializedName;

public class RedemptionDetailsResponse
{
    @SerializedName("claim_details")
    private RedemptionDetails mRedemptionDetails;

    public RedemptionDetails getRedemptionDetails()
    {
        return mRedemptionDetails;
    }
}
