package com.handybook.handybook.logger.handylogger.model.user;

import com.google.gson.annotations.SerializedName;
import com.handybook.handybook.logger.handylogger.model.EventLog;
import com.handybook.handybook.logger.handylogger.model.booking.EventContext;
import com.handybook.handybook.logger.handylogger.model.booking.EventType;

public abstract class ReferralLog extends EventLog {

    public ReferralLog(final String eventType) {
        super(eventType, EventContext.NATIVE_SHARE);
    }

    public static class ReferralOpenLog extends ReferralLog {

        @SerializedName("page")
        private String mPage = "referral";

        public ReferralOpenLog() {
            super(EventType.NAVIGATION);
        }
    }
}
