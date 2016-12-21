package com.handybook.handybook.core.model.request;

import com.google.gson.annotations.SerializedName;

public class CreateUserRequest
{
    @SerializedName("email")
    private String mEmail;
    @SerializedName("password")
    private String mPassword;
    @SerializedName("first_name")
    private String mFirstName;
    @SerializedName("last_name")
    private String mLastName;
    @SerializedName("referral_post_guid")
    private String mReferralPostGuid;

    @SerializedName("uid")
    private String mFacebookUserId;
    @SerializedName("facebook_access_token")
    private String mFacebookAccessToken;

    public void setEmail(String email)
    {
        mEmail = email;
    }

    public void setPassword(String password)
    {
        mPassword = password;
    }

    public void setFirstName(String firstName)
    {
        mFirstName = firstName;
    }

    public void setLastName(String lastName)
    {
        mLastName = lastName;
    }

    public void setReferralPostGuid(String referralPostGuid)
    {
        mReferralPostGuid = referralPostGuid;
    }

    public void setFacebookUserId(String facebookUserId)
    {
        mFacebookUserId = facebookUserId;
    }

    public void setFacebookAccessToken(String facebookAccessToken)
    {
        mFacebookAccessToken = facebookAccessToken;
    }
}
