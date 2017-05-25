package com.handybook.handybook.logger.handylogger.model.account;

import com.google.gson.annotations.SerializedName;
import com.handybook.handybook.logger.handylogger.model.EventLog;

public class EditPlanHoursLog extends EventLog {

    private static final String EVENT_CONTEXT = "plan_management";
    @SerializedName("recurring_booking_id")
    private final Integer mRecurringBookingId;
    @SerializedName("hours")
    private final Double mHours;

    private EditPlanHoursLog(
            final String eventType,
            final Integer recurringBookingId,
            final Double hours
    ) {
        super(eventType, EVENT_CONTEXT);
        mRecurringBookingId = recurringBookingId;
        mHours = hours;
    }

    public static class Shown extends EditPlanHoursLog {

        private static final String EVENT_TYPE = "edit_hours_shown";

        public Shown(final Integer recurringBookingId, final Double hours) {
            super(EVENT_TYPE, recurringBookingId, hours);
        }
    }


    public static class Submitted extends EditPlanHoursLog {

        private static final String EVENT_TYPE = "edit_hours_submitted";

        public Submitted(
                final Integer recurringBookingId,
                final Double hours
        ) {
            super(EVENT_TYPE, recurringBookingId, hours);
        }
    }


    public static class Success extends EditPlanHoursLog {

        private static final String EVENT_TYPE = "edit_hours_success";

        public Success(final Integer recurringBookingId, final Double hours) {
            super(EVENT_TYPE, recurringBookingId, hours);
        }
    }


    public static class Error extends EditPlanHoursLog {

        private static final String EVENT_TYPE = "edit_hours_error";
        @SerializedName("error_info")
        private final String mErrorInfo;

        public Error(final Integer recurringBookingId, final Double hours, final String errorInfo) {
            super(EVENT_TYPE, recurringBookingId, hours);
            mErrorInfo = errorInfo;
        }
    }


}
