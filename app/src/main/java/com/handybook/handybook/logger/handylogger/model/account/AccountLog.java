package com.handybook.handybook.logger.handylogger.model.account;

import com.google.gson.annotations.SerializedName;
import com.handybook.handybook.logger.handylogger.model.EventLog;

/**
 * Created by sng on 9/22/16.
 */

public abstract class AccountLog extends EventLog {

    private static final String EVENT_CONTEXT = "account";

    private AccountLog(final String eventType) {
        super(eventType, EVENT_CONTEXT);
    }

    /**
     * Always log when this page is shown.
     * 1. When you come back from background
     * 2. When you come to Page shows the first time
     * 3. When you click away from page, but now you hit back and page is shown again
     */
    public static class Shown extends AccountLog {

        private static final String EVENT_TYPE = "shown";

        public Shown() {
            super(EVENT_TYPE);
        }
    }


    public static class EditProfileTapped extends AccountLog {

        private static final String EVENT_TYPE = "edit_profile_tapped";

        public EditProfileTapped() {
            super(EVENT_TYPE);
        }
    }


    public static class EditPasswordTapped extends AccountLog {

        private static final String EVENT_TYPE = "edit_password_tapped";

        public EditPasswordTapped() {
            super(EVENT_TYPE);
        }
    }


    public static class EditPaymentTapped extends AccountLog {

        private static final String EVENT_TYPE = "edit_payment_tapped";

        public EditPaymentTapped() {
            super(EVENT_TYPE);
        }
    }


    public static class ApplyPromoTapped extends AccountLog {

        private static final String EVENT_TYPE = "apply_promo_tapped";

        public ApplyPromoTapped() {
            super(EVENT_TYPE);
        }
    }


    public static class PlanManagementTapped extends AccountLog {

        private static final String EVENT_TYPE = "plan_management_tapped";

        @SerializedName("plan_count")
        private final int mPlanCount;

        public PlanManagementTapped(int planCount) {
            super(EVENT_TYPE);

            mPlanCount = planCount;
        }
    }


    public static class LogoutTapped extends AccountLog {

        private static final String EVENT_TYPE = "logout_tapped";

        public LogoutTapped() {
            super(EVENT_TYPE);
        }
    }


    public static class LogoutSuccess extends AccountLog {

        private static final String EVENT_TYPE = "logout_success";

        public LogoutSuccess() {
            super(EVENT_TYPE);
        }
    }


    public static class LogoutCancelled extends AccountLog {

        private static final String EVENT_TYPE = "logout_cancelled";

        public LogoutCancelled() {
            super(EVENT_TYPE);
        }
    }

    // Contact Info Logs


    public static class ContactInfoShown extends AccountLog {

        private static final String EVENT_TYPE = "contact_info_shown";

        public ContactInfoShown() {
            super(EVENT_TYPE);
        }
    }


    public static class ContactInfoUpdateTapped extends AccountLog {

        private static final String EVENT_TYPE = "contact_info_update_tapped";

        public ContactInfoUpdateTapped() {
            super(EVENT_TYPE);
        }
    }


    public static class ContactInfoUpdateSuccess extends AccountLog {

        private static final String EVENT_TYPE = "contact_info_update_success";

        public ContactInfoUpdateSuccess() {
            super(EVENT_TYPE);
        }
    }


    public static class ContactInfoUpdateError extends AccountLog {

        private static final String EVENT_TYPE = "contact_info_update_error";

        public ContactInfoUpdateError() {
            super(EVENT_TYPE);
        }
    }

    // Update Password Logs


    public static class UpdatePasswordShown extends AccountLog {

        private static final String EVENT_TYPE = "update_password_shown";

        public UpdatePasswordShown() {
            super(EVENT_TYPE);
        }
    }


    public static class UpdatePasswordTapped extends AccountLog {

        private static final String EVENT_TYPE = "update_password_update_tapped";

        public UpdatePasswordTapped() {
            super(EVENT_TYPE);
        }
    }


    public static class UpdatePasswordSuccess extends AccountLog {

        private static final String EVENT_TYPE = "update_password_update_success";

        public UpdatePasswordSuccess() {
            super(EVENT_TYPE);
        }
    }


    public static class UpdatePasswordError extends AccountLog {

        private static final String EVENT_TYPE = "update_password_update_error";

        public UpdatePasswordError() {
            super(EVENT_TYPE);
        }
    }

    // Edit Payment Methods Logs


    public static class PaymentMethodShown extends AccountLog {

        private static final String EVENT_TYPE = "edit_payment_shown";

        public PaymentMethodShown() {
            super(EVENT_TYPE);
        }
    }


    public static class PaymentMethodUpdateTapped extends AccountLog {

        private static final String EVENT_TYPE = "edit_payment_update_tapped";

        public PaymentMethodUpdateTapped() {
            super(EVENT_TYPE);
        }
    }


    public static class PaymentMethodUpdateSuccess extends AccountLog {

        private static final String EVENT_TYPE = "edit_payment_change_success";

        public PaymentMethodUpdateSuccess() {
            super(EVENT_TYPE);
        }
    }


    public static class PaymentMethodUpdateError extends AccountLog {

        private static final String EVENT_TYPE = "edit_payment_change_error";

        public PaymentMethodUpdateError() {
            super(EVENT_TYPE);
        }
    }


    //Help section
    public static class HelpTapped extends AccountLog {

        private static final String EVENT_TYPE = "help_tapped";

        public HelpTapped() {
            super(EVENT_TYPE);
        }
    }


    //Help section
    public static class BookingHistoryTapped extends AccountLog {

        private static final String EVENT_TYPE = "booking_history_tapped";

        public BookingHistoryTapped() {
            super(EVENT_TYPE);
        }
    }
}
