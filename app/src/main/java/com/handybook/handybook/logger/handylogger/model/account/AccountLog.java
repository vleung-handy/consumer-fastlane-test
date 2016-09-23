package com.handybook.handybook.logger.handylogger.model.account;

import com.google.gson.annotations.SerializedName;
import com.handybook.handybook.logger.handylogger.model.EventLog;

/**
 * Created by sng on 9/22/16.
 */

public class AccountLog extends EventLog
{
    private static final String EVENT_CONTEXT = "account";

    private AccountLog(final String eventType)
    {
        super(eventType, EVENT_CONTEXT);
    }

    public static class Shown extends AccountLog
    {
        private static final String EVENT_TYPE = "shown";

        public Shown()
        {
            super(EVENT_TYPE);
        }
    }


    public static class EditProfileTapped extends AccountLog
    {
        private static final String EVENT_TYPE = "edit_profile_tapped";

        public EditProfileTapped()
        {
            super(EVENT_TYPE);
        }
    }


    public static class EditPasswordTapped extends AccountLog
    {
        private static final String EVENT_TYPE = "edit_password_tapped";

        public EditPasswordTapped()
        {
            super(EVENT_TYPE);
        }
    }


    public static class EditPaymentTapped extends AccountLog
    {
        private static final String EVENT_TYPE = "edit_payment_tapped";

        public EditPaymentTapped()
        {
            super(EVENT_TYPE);
        }
    }


    public static class PlanManagementTapped extends AccountLog
    {
        private static final String EVENT_TYPE = "plan_management_tapped";

        @SerializedName("plan_count")
        private final int mPlanCount;

        public PlanManagementTapped(int planCount)
        {
            super(EVENT_TYPE);

            mPlanCount = planCount;
        }
    }


    public static class LogoutTapped extends AccountLog
    {
        private static final String EVENT_TYPE = "logout_tapped";

        public LogoutTapped()
        {
            super(EVENT_TYPE);
        }
    }


    public static class LogoutConfirmed extends AccountLog
    {
        private static final String EVENT_TYPE = "logout_confirmed";

        public LogoutConfirmed()
        {
            super(EVENT_TYPE);
        }
    }


    public static class LogoutCancelled extends AccountLog
    {
        private static final String EVENT_TYPE = "logout_cancelled";

        public LogoutCancelled()
        {
            super(EVENT_TYPE);
        }
    }
}
