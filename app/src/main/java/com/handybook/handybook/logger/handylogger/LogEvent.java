package com.handybook.handybook.logger.handylogger;

import com.handybook.handybook.core.event.HandyEvent;
import com.handybook.handybook.logger.handylogger.model.EventLog;

public abstract class LogEvent extends HandyEvent {

    public static class AddLogEvent extends RequestEvent {

        private final EventLog mLog;

        public AddLogEvent(EventLog log) {mLog = log;}

        public EventLog getLog() {
            return mLog;
        }
    }
}
