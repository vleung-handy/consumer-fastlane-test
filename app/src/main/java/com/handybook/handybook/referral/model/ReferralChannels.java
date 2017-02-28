package com.handybook.handybook.referral.model;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringDef;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Holds ReferralInfo objects for each supported channel used for in-app sharing. Supported
 * channels include SMS, Facebook, Twitter, etc.
 */
public class ReferralChannels implements Serializable {

    public static final String CHANNEL_EMAIL = "email";
    public static final String CHANNEL_GMAIL = "gmail";
    public static final String CHANNEL_GPLUS = "gplus";
    public static final String CHANNEL_FACEBOOK = "facebook";
    public static final String CHANNEL_TWITTER = "twitter";
    public static final String CHANNEL_SMS = "sms";
    public static final String CHANNEL_OTHER = "other";

    @SerializedName(CHANNEL_EMAIL)
    private ReferralInfo mEmailReferralInfo;
    @SerializedName(CHANNEL_GMAIL)
    private ReferralInfo mGmailReferralInfo;
    @SerializedName(CHANNEL_GPLUS)
    private ReferralInfo mGplusReferralInfo;
    @SerializedName(CHANNEL_FACEBOOK)
    private ReferralInfo mFacebookReferralInfo;
    @SerializedName(CHANNEL_TWITTER)
    private ReferralInfo mTwitterReferralInfo;
    @SerializedName(CHANNEL_SMS)
    private ReferralInfo mSmsReferralInfo;


    @Retention(RetentionPolicy.SOURCE)
    @StringDef({
                       CHANNEL_EMAIL,
                       CHANNEL_GMAIL,
                       CHANNEL_GPLUS,
                       CHANNEL_FACEBOOK,
                       CHANNEL_TWITTER,
                       CHANNEL_SMS,
               })
    public @interface Channel {}

    /**
     * Returns ReferralInfo object associated with the supported channel provided.
     *
     * @param channel a supported channel
     * @return the object associated with the channel provided
     * @see ReferralInfo
     * @see Channel
     */
    @Nullable
    public ReferralInfo getReferralInfoForChannel(@NonNull @Channel final String channel) {
        switch (channel) {
            case CHANNEL_EMAIL:
                return mEmailReferralInfo;
            case CHANNEL_GMAIL:
                return mGmailReferralInfo;
            case CHANNEL_GPLUS:
                return mGplusReferralInfo;
            case CHANNEL_FACEBOOK:
                return mFacebookReferralInfo;
            case CHANNEL_TWITTER:
                return mTwitterReferralInfo;
            case CHANNEL_SMS:
                return mSmsReferralInfo;
            default:
                return null;
        }
    }
}
