package com.handybook.handybook.model.logging.user;

import com.handybook.handybook.model.logging.EventLog;


public class UserContactLog extends EventLog
{
    private static final String EVENT_CONTEXT = "contact";

    public UserContactLog(final String eventType)
    {
        super(eventType, EVENT_CONTEXT);
    }


    public static class UserContactShownLog extends UserContactLog
    {
        private static final String EVENT_TYPE = "shown";

        public UserContactShownLog()
        {
            super(EVENT_TYPE);
        }
    }
}
