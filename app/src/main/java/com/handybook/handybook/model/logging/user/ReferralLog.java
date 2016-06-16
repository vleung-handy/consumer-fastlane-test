package com.handybook.handybook.model.logging.user;

import com.handybook.handybook.model.logging.EventLog;

public abstract class ReferralLog extends EventLog
{
    private static final String EVENT_CONTEXT = "referral";

    public ReferralLog(final String eventType)
    {
        super(eventType, EVENT_CONTEXT);
    }


    public static class ReferralOpenLog extends ReferralLog
    {
        private static final String EVENT_TYPE = "open";

        public ReferralOpenLog()
        {
            super(EVENT_TYPE);
        }
    }
}
