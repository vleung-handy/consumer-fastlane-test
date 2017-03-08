package com.handybook.handybook.booking.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class ChatOptions implements Serializable {

    @SerializedName("allow_chat")
    private boolean mAllowChat;
    @SerializedName("direct_to_in_app_chat")
    private boolean mDirectToInAppChat;

    public boolean isAllowChat() {
        return mAllowChat;
    }

    public boolean isDirectToInAppChat() {
        return mDirectToInAppChat;
    }

    public void setAllowChat(final boolean allowChat) {
        mAllowChat = allowChat;
    }

    public void setDirectToInAppChat(final boolean directToInAppChat) {
        mDirectToInAppChat = directToInAppChat;
    }
}
