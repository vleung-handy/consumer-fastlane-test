package com.handybook.handybook.model.logging.user;


import com.handybook.handybook.model.logging.EventLog;

public class UserLoginLog extends EventLog
{
    private static final String EVENT_CONTEXT = "login";

    public UserLoginLog(final String eventType)
    {
        super(eventType, EVENT_CONTEXT);
    }


    public static class LoginLog extends UserLoginLog
    {
        private static final String EVENT_TYPE = "log";

        public LoginLog()
        {
            super(EVENT_TYPE);
        }
    }
}
