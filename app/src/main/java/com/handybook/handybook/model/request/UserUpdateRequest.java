package com.handybook.handybook.model.request;

import com.google.gson.annotations.SerializedName;

public class UserUpdateRequest
{
//        @SerializedName("user")
//        private User user;
//        @SerializedName("auth_token")
//        private String authToken;

    public String getUserId()
    {
        return mUserId;
    }

    public String getFirstName()
    {
        return mFirstName;
    }

    public String getLastName()
    {
        return mLastName;
    }

    public String getEmail()
    {
        return mEmail;
    }

    public String getPhone()
    {
        return mPhone;
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

    @SerializedName("id")
    private String mUserId;
    @SerializedName("first_name")
    private String mFirstName;
    @SerializedName("last_name")
    private String mLastName;
    @SerializedName("email")
    private String mEmail;
    @SerializedName("phone")
    private String mPhone;

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

    @SerializedName("password") private String mPassword;
    @SerializedName("current_password") private String mCurrentPassword;
    @SerializedName("password_confirmation") private String mPasswordConfirmation;

    public UserUpdateRequest()
    {

    }
}