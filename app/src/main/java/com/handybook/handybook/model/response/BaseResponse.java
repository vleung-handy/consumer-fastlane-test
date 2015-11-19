package com.handybook.handybook.model.response;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class BaseResponse
{
    @SerializedName("error")
    protected boolean mError;
    @SerializedName("messages")
    protected List<String> mMessages;

    public boolean isError()
    {
        return mError;
    }

    public List<String> getMessages()
    {
        return mMessages;
    }
}
