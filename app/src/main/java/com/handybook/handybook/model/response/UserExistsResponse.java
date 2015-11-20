package com.handybook.handybook.model.response;

import com.google.gson.annotations.SerializedName;

public class UserExistsResponse
{
    @SerializedName("exists")
    private boolean mExists;
    @SerializedName("first_name")
    private String mFirstName;

    public boolean exists()
    {
        return mExists;
    }

    public String getFirstName()
    {
        return mFirstName;
    }
}
