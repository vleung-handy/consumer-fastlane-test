package com.handybook.handybook.logger.handylogger.model;

import com.google.gson.annotations.SerializedName;
import com.handybook.handybook.logger.handylogger.constants.EventContext;
import com.handybook.handybook.logger.handylogger.constants.EventType;

public abstract class ConversationsLog extends EventLog {

    private ConversationsLog(final String eventType) {
        super(eventType, EventContext.CONVERSATIONS);
    }

    public static class LoadingError extends ConversationsLog {

        @SerializedName("error_info")
        private String mErrorInfo;

        public LoadingError(final String errorInfo) {
            super(EventType.CONVERSATIONS_LOADING_ERROR);
            mErrorInfo = errorInfo;
        }
    }


    public static class Loaded extends ConversationsLog {

        @SerializedName("total_conversations_count")
        private int mTotalConversationsCount;

        public Loaded(final int totalConversationsCount) {
            super(EventType.CONVERSATIONS_LOADED);
            mTotalConversationsCount = totalConversationsCount;
        }
    }
}
