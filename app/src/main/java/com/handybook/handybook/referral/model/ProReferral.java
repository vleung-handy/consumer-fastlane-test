package com.handybook.handybook.referral.model;

import android.support.annotation.Nullable;

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

    @SerializedName("header")
    private String mHeader;

    @SerializedName("subtitle")
    private String mSubTitle;

    @SerializedName("share_url")
    private String mShareUrl;

    @SerializedName("referral_button_text")
    private String mReferralButtonText;

    @SerializedName("referral_info")
    private ReferralChannels mReferralInfo;

    @Nullable
    public Provider getProvider() {
        return mProvider;
    }

    public void setProvider(final Provider provider) {
        mProvider = provider;
    }

    public ReferralChannels getReferralInfo() {
        return mReferralInfo;
    }

    public String getReferralButtonText() {
        return mReferralButtonText;
    }

    public String getHeader() {
        return mHeader;
    }

    public String getSubTitle() {
        return mSubTitle;
    }

    @Nullable
    public String getShareUrl() {
        return mShareUrl;
    }

}
