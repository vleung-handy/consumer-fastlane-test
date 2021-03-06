package com.handybook.handybook.logger.handylogger.model.booking;

import com.google.gson.annotations.SerializedName;
import com.handybook.handybook.logger.handylogger.model.EventLog;

public abstract class AddressAutocompleteLog extends EventLog {

    private static final String EVENT_CONTEXT = "address_autocomplete";

    public AddressAutocompleteLog(final String eventType) {
        super(eventType, EVENT_CONTEXT);
    }

    /**
     * Triggered when a request to the auto complete service is made.
     */
    public static class AddressAutocompleteRequestLog extends AddressAutocompleteLog {

        private static final String EVENT_TYPE = "address_autocomplete_request";

        @SerializedName("prediction_query")
        private String mQuery;

        public AddressAutocompleteRequestLog(String query) {
            super(EVENT_TYPE);
            mQuery = query;
        }
    }


    /**
     * Triggered when a response is obtained from the auto complete service.
     */
    public static class AddressAutocompleteResponseLog extends AddressAutocompleteLog {

        private static final String EVENT_TYPE = "address_autocomplete_response";

        @SerializedName("prediction_count")
        private int mPredictionCount;

        public AddressAutocompleteResponseLog(int count) {
            super(EVENT_TYPE);

            mPredictionCount = count;
        }
    }


    /**
     * Triggered when a user selects one of the options displayed for autocomplete
     */
    public static class AddressAutocompleteItemTappedLog extends AddressAutocompleteLog {

        private static final String EVENT_TYPE = "address_autocomplete_item_tapped";

        @SerializedName("item")
        private String mItem;

        public AddressAutocompleteItemTappedLog(String item) {
            super(EVENT_TYPE);
            mItem = item;
        }
    }

}
