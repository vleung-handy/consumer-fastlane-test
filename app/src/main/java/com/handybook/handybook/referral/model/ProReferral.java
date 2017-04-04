package com.handybook.handybook.referral.model;

import com.google.gson.annotations.SerializedName;
import com.handybook.handybook.booking.model.Provider;

import java.io.Serializable;

/**
 * A model object to hold the response that comes back from the
 *
 * /referrals/prepare?proteam=true
 *
 * endpoint.
 */
public class ProReferral implements Serializable {

    @SerializedName("provider")
    private Provider mProvider;

    //TODO: JIA: Ankit to add these
    private String mTitle;
    private String mSubTitle;

    @SerializedName("referral_button_text")
    private String mReferralButtonText;

    @SerializedName("referral_info")
    private ReferralChannels mReferralInfo;

    public Provider getProvider() {
        return mProvider;
    }

    public ReferralChannels getReferralInfo() {
        return mReferralInfo;
    }

    public String getReferralButtonText() {
        return mReferralButtonText;
    }

    public String getTitle() {
        return mTitle;
    }

    public String getSubTitle() {
        return mSubTitle;
    }
}
