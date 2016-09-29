package com.handybook.handybook.logger.handylogger.model.account;

import com.google.gson.annotations.SerializedName;
import com.handybook.handybook.logger.handylogger.model.EventLog;

/**
 * Created by sng on 9/22/16.
 */

public class PlanSelectionLog extends EventLog
{
    private static final String EVENT_CONTEXT = "plan_management";

    protected PlanSelectionLog(final String eventType)
    {
        super(eventType, EVENT_CONTEXT);
    }

    public static class Shown extends PlanSelectionLog
    {
        private static final String EVENT_TYPE = "select_plan_shown";

        @SerializedName("recurring_booking_ids")
        private final int[] mRecurringBookingIds;

        public Shown(int[] recurringBookingIds)
        {
            super(EVENT_TYPE);

            mRecurringBookingIds = recurringBookingIds;
        }
    }

    public static class PlanTapped extends PlanSelectionLog
    {
        private static final String EVENT_TYPE = "select_plan_tapped";

        @SerializedName("recurring_booking_id")
        private final int mRecurringBookingId;

        public PlanTapped(int recurringBookingId)
        {
            super(EVENT_TYPE);

            mRecurringBookingId = recurringBookingId;
        }
    }
}
