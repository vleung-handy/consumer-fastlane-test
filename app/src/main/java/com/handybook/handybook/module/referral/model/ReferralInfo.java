package com.handybook.handybook.module.referral.model;

import com.google.gson.annotations.SerializedName;

public class ReferralInfo
{
    @SerializedName("guid")
    private String mGuid;
    @SerializedName("share_subject")
    private String mSubject;
    @SerializedName("share_msg")
    private String mMessage;
    @SerializedName("share_url")
    private String mUrl;

    public String getGuid()
    {
        return mGuid;
    }

    public String getSubject()
    {
        return mSubject;
    }

    public String getMessage()
    {
        return mMessage;
    }

    public String getUrl()
    {
        return mUrl;
    }
}