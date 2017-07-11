package com.handybook.handybook.logger.handylogger.model.account;

import com.handybook.handybook.logger.handylogger.model.EventLog;

/**
 * Created by sng on 9/22/16.
 */

public abstract class EditAddressLog extends EventLog {

    private static final String EVENT_CONTEXT = "plan_management";

    private EditAddressLog(final String eventType) {
        super(eventType, EVENT_CONTEXT);
    }

    public static class Shown extends EditAddressLog {

        private static final String EVENT_TYPE = "edit_address_shown";

        public Shown() {
            super(EVENT_TYPE);
        }
    }


    public static class Submitted extends EditAddressLog {

        private static final String EVENT_TYPE = "edit_address_submitted";

        public Submitted() {
            super(EVENT_TYPE);
        }
    }


    public static class Success extends EditAddressLog {

        private static final String EVENT_TYPE = "edit_address_success";

        public Success() {
            super(EVENT_TYPE);
        }
    }


    public static class Error extends EditAddressLog {

        private static final String EVENT_TYPE = "edit_address_error";

        public Error() {
            super(EVENT_TYPE);
        }
    }
}
