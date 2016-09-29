package com.handybook.handybook.logger.handylogger.model.account;

import com.google.gson.annotations.SerializedName;
import com.handybook.handybook.logger.handylogger.model.EventLog;

/**
 * Created by sng on 9/22/16.
 */

public class EditPlanLog extends EventLog
{
    private static final String EVENT_CONTEXT = "plan_management";

    private EditPlanLog(final String eventType)
    {
        super(eventType, EVENT_CONTEXT);
    }

    public static class Shown extends EditPlanLog
    {
        private static final String EVENT_TYPE = "edit_plan_shown";

        @SerializedName("recurring_booking_id")
        private final int mRecurringBookingId;

        public Shown(int recurringBookingId)
        {
            super(EVENT_TYPE);

            mRecurringBookingId = recurringBookingId;
        }
    }


    public static class EditFrequencyTapped extends EditPlanLog
    {
        private static final String EVENT_TYPE = "edit_frequency_tapped";

        @SerializedName("recurring_booking_id")
        private final int mRecurringBookingId;
        @SerializedName("frequency")
        private final int mFrequency;

        public EditFrequencyTapped(int recurringBookingId, int frequency)
        {
            super(EVENT_TYPE);

            mRecurringBookingId = recurringBookingId;
            mFrequency = frequency;
        }
    }


    public static class EditAddressTapped extends EditPlanLog
    {
        private static final String EVENT_TYPE = "edit_address_tapped";

        @SerializedName("recurring_booking_id")
        private final int mRecurringBookingId;

        public EditAddressTapped(int recurringBookingId)
        {
            super(EVENT_TYPE);

            mRecurringBookingId = recurringBookingId;
        }
    }


    public static class CancelPlanTapped extends EditPlanLog
    {
        private static final String EVENT_TYPE = "cancel_plan_tapped";

        @SerializedName("recurring_booking_id")
        private final int mRecurringBookingId;

        public CancelPlanTapped(int recurringBookingId)
        {
            super(EVENT_TYPE);

            mRecurringBookingId = recurringBookingId;
        }
    }
}
