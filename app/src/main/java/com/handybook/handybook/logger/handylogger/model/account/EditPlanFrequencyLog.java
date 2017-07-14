package com.handybook.handybook.logger.handylogger.model.account;

import com.google.gson.annotations.SerializedName;
import com.handybook.handybook.logger.handylogger.model.EventLog;

/**
 * Created by sng on 9/22/16.
 */

public abstract class EditPlanFrequencyLog extends EventLog {

    private static final String EVENT_CONTEXT = "plan_management";

    private EditPlanFrequencyLog(final String eventType) {
        super(eventType, EVENT_CONTEXT);
    }

    public static class Shown extends EditPlanFrequencyLog {

        private static final String EVENT_TYPE = "edit_frequency_shown";

        @SerializedName("recurring_booking_id")
        private final int mRecurringBookingId;
        @SerializedName("old_frequency")
        private final int mOldFrequency;

        public Shown(int recurringBookingId, int oldFrequency) {
            super(EVENT_TYPE);

            mRecurringBookingId = recurringBookingId;
            mOldFrequency = oldFrequency;
        }
    }


    public static class Submitted extends EditPlanFrequencyLog {

        private static final String EVENT_TYPE = "edit_frequency_submitted";

        @SerializedName("recurring_booking_id")
        private final int mRecurringBookingId;
        @SerializedName("old_frequency")
        private final int mOldFrequency;
        @SerializedName("new_frequency")
        private final int mNewFrequency;

        public Submitted(int recurringBookingId, int oldFrequency, int newFrequency) {
            super(EVENT_TYPE);

            mRecurringBookingId = recurringBookingId;
            mOldFrequency = oldFrequency;
            mNewFrequency = newFrequency;
        }
    }


    public static class Success extends EditPlanFrequencyLog {

        private static final String EVENT_TYPE = "edit_frequency_success";

        @SerializedName("recurring_booking_id")
        private final int mRecurringBookingId;
        @SerializedName("old_frequency")
        private final int mOldFrequency;
        @SerializedName("new_frequency")
        private final int mNewFrequency;

        public Success(int recurringBookingId, int oldFrequency, int newFrequency) {
            super(EVENT_TYPE);

            mRecurringBookingId = recurringBookingId;
            mOldFrequency = oldFrequency;
            mNewFrequency = newFrequency;
        }
    }


    public static class Error extends EditPlanFrequencyLog {

        private static final String EVENT_TYPE = "edit_frequency_error";

        @SerializedName("recurring_booking_id")
        private final int mRecurringBookingId;
        @SerializedName("old_frequency")
        private final int mOldFrequency;
        @SerializedName("new_frequency")
        private final int mNewFrequency;

        public Error(int recurringBookingId, int oldFrequency, int newFrequency) {
            super(EVENT_TYPE);

            mRecurringBookingId = recurringBookingId;
            mOldFrequency = oldFrequency;
            mNewFrequency = newFrequency;
        }
    }
}
