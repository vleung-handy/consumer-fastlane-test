package com.handybook.handybook.model.request;

import com.google.gson.annotations.SerializedName;

public class UpdateUserRequest
{
    @SerializedName("id")
    private String mUserId; //this is set in the request path for logging purposes
    @SerializedName("first_name")
    private String mFirstName;
    @SerializedName("last_name")
    private String mLastName;
    @SerializedName("email")
    private String mEmail;
    @SerializedName("phone")
    private String mPhone;
    @SerializedName("password")
    private String mPassword;
    @SerializedName("current_password")
    private String mCurrentPassword;
    @SerializedName("password_confirmation")
    private String mPasswordConfirmation;

    public String getUserId()
    {
        return mUserId;
    }

    public void setUserId(final String userId)
    {
        mUserId = userId;
    }

    public void setFirstName(final String firstName)
    {
        mFirstName = firstName;
    }

    public void setLastName(final String lastName)
    {
        mLastName = lastName;
    }

    public void setEmail(final String email)
    {
        mEmail = email;
    }

    public void setPhone(final String phone)
    {
        mPhone = phone;
    }

    public void setPassword(final String password)
    {
        mPassword = password;
    }

    public void setCurrentPassword(final String currentPassword)
    {
        mCurrentPassword = currentPassword;
    }

    public void setPasswordConfirmation(final String passwordConfirmation)
    {
        mPasswordConfirmation = passwordConfirmation;
    }
}
