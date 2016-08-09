package com.handybook.handybook.booking.model;


import com.google.common.base.Strings;
import com.google.gson.annotations.SerializedName;
import com.handybook.handybook.util.StringUtils;

import java.io.Serializable;

public class Provider implements Serializable
{
    @SerializedName("status")
    private int mStatus;
    @SerializedName("first_name")
    private String mFirstName;
    @SerializedName("last_name")
    private String mLastName;
    @SerializedName("phone")
    private String mPhone;

    public Provider(final int status, final String firstName, final String lastName, final String phone)
    {
        mStatus = status;
        mFirstName = firstName;
        mLastName = lastName;
        mPhone = phone;
    }

    public final int getStatus()
    {
        return mStatus;
    }

    final void setStatus(final int status)
    {
        this.mStatus = status;
    }

    public final String getFirstName()
    {
        return mFirstName;
    }

    final void setFirstName(final String firstName)
    {
        this.mFirstName = firstName;
    }

    public final String getLastName()
    {
        return mLastName;
    }

    final void setLastName(final String lastName)
    {
        this.mLastName = lastName;
    }

    public final String getPhone()
    {
        return mPhone;
    }

    final void setPhone(final String phone)
    {
        this.mPhone = phone;
    }

    /**
     * TODO temporary logic. eventually make the server return this
     * @return the provider's first name and last initial in the format: Aaaaa B.
     *
     */
    public final String getFirstNameAndLastInitial()
    {
        String firstNameAndLastInitial = "";
        if (!Strings.isNullOrEmpty(mFirstName))
        {
            firstNameAndLastInitial += StringUtils.capitalizeFirstCharacter(mFirstName);
        }
        if (!Strings.isNullOrEmpty(mLastName))
        {
            firstNameAndLastInitial += (" " + Character.toUpperCase(mLastName.charAt(0)) + ".");
        }
        return firstNameAndLastInitial;
    }

    public final String getFullName()
    {
        return (mFirstName != null ? mFirstName : "") + " " + (mLastName != null ? mLastName : "");
    }

    public static final int PROVIDER_STATUS_ASSIGNED = 3; //TODO: Not sure what this is, just conjecturing based on code
}
