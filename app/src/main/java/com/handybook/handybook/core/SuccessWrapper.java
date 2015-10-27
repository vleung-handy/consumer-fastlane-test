package com.handybook.handybook.core;

import com.google.gson.annotations.SerializedName;

public class SuccessWrapper
{
    @SerializedName("success")
    private boolean mSuccess;

    public final boolean getSuccess()
    {
        return mSuccess;
    }
}
