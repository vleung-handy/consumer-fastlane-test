package com.handybook.handybook.proteam.callback;

public interface ConversationCallback {

    void onCreateConversationSuccess(String conversationId);

    void onCreateConversationError();
}
