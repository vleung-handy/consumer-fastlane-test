package com.handybook.handybook.logger.handylogger.model.booking;

import com.handybook.handybook.logger.handylogger.model.EventLog;


public class BookingCommentsLog extends EventLog
{
    private static final String EVENT_CONTEXT = "comments";

    public BookingCommentsLog(final String eventType)
    {
        super(eventType, EVENT_CONTEXT);
    }


}
