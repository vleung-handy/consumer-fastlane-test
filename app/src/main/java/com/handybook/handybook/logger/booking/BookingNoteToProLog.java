package com.handybook.handybook.logger.booking;

import com.handybook.handybook.logger.EventLog;

public class BookingNoteToProLog extends EventLog
{
    private static final String EVENT_CONTEXT = "shareInfoWithProfessional";

    public BookingNoteToProLog(final String eventType)
    {
        super(eventType, EVENT_CONTEXT);
    }

    public static class BookingNoteToProShownLog extends BookingNoteToProLog
    {
        private static final String EVENT_TYPE = "shown";

        public BookingNoteToProShownLog()
        {
            super(EVENT_TYPE);
        }
    }
}

