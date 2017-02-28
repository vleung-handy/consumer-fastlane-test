package com.handybook.handybook.core;

import com.google.gson.annotations.SerializedName;

//TODO: need to reorganize packages and put this class in the proper place
public class SuccessWrapper {

    @SerializedName("success")
    private boolean mSuccess;

    public final boolean getSuccess() {
        return mSuccess;
    }
}
