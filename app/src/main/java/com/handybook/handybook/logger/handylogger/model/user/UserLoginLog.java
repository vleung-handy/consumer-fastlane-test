package com.handybook.handybook.logger.handylogger.model.user;


import com.google.gson.annotations.SerializedName;
import com.handybook.handybook.logger.handylogger.model.EventLog;

public class UserLoginLog extends EventLog
{
    private static final String EVENT_CONTEXT = "login";

    public UserLoginLog(final String eventType)
    {
        super(eventType, EVENT_CONTEXT);
    }


    public static class UserLoginShownLog extends UserLoginLog
    {
        private static final String EVENT_TYPE = "shown";

        public UserLoginShownLog()
        {
            super(EVENT_TYPE);
        }
    }


    public static class UserLoginSubmittedLog extends UserLoginLog
    {
        private static final String EVENT_TYPE = "submitted";

        @SerializedName("email")
        private String mEmail;

        public UserLoginSubmittedLog(final String email)
        {
            super(EVENT_TYPE);
            mEmail = email;
        }
    }
}
