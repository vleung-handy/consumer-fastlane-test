package com.handybook.handybook.core.model.response;

import com.google.gson.annotations.SerializedName;

public class ErrorResponse {

    @SerializedName("error")
    private boolean mError;
    @SerializedName("code")
    private Integer mErrorCode;
    @SerializedName("messages")
    private String[] messages;
    @SerializedName("invalid_inputs")
    private String[] invalidInputs;

    public boolean isError() {
        return mError;
    }

    public Integer getErrorCode() {
        return mErrorCode;
    }

    public String[] getMessages() {
        return messages;
    }

    public String[] getInvalidInputs() {
        return invalidInputs;
    }
}
