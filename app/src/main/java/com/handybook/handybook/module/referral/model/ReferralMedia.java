package com.handybook.handybook.module.referral.model;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringDef;

import com.google.gson.annotations.SerializedName;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class ReferralMedia
{
    public static final String EMAIL = "email";
    public static final String GMAIL = "gmail";
    public static final String GPLUS = "gplus";
    public static final String FACEBOOK = "facebook";
    public static final String TWITTER = "twitter";
    public static final String SMS = "sms";

    @SerializedName(EMAIL)
    private ReferralInfo mEmailReferralInfo;
    @SerializedName(GMAIL)
    private ReferralInfo mGmailReferralInfo;
    @SerializedName(GPLUS)
    private ReferralInfo mGplusReferralInfo;
    @SerializedName(FACEBOOK)
    private ReferralInfo mFacebookReferralInfo;
    @SerializedName(TWITTER)
    private ReferralInfo mTwitterReferralInfo;
    @SerializedName(SMS)
    private ReferralInfo mSmsReferralInfo;

    @Retention(RetentionPolicy.SOURCE)
    @StringDef({EMAIL, GMAIL, GPLUS, FACEBOOK, TWITTER, SMS})
    public @interface Medium {}

    @Nullable
    public ReferralInfo getReferralInfo(@NonNull @Medium final String medium)
    {
        switch (medium)
        {
            case EMAIL:
                return mEmailReferralInfo;
            case GMAIL:
                return mGmailReferralInfo;
            case GPLUS:
                return mGplusReferralInfo;
            case FACEBOOK:
                return mFacebookReferralInfo;
            case TWITTER:
                return mTwitterReferralInfo;
            case SMS:
                return mSmsReferralInfo;
            default:
                return null;
        }
    }
}
