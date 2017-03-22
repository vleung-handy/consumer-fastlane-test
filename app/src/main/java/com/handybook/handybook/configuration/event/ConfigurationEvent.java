package com.handybook.handybook.configuration.event;

import com.handybook.handybook.configuration.model.Configuration;
import com.handybook.handybook.core.data.DataManager;
import com.handybook.handybook.core.event.HandyEvent;

public class ConfigurationEvent {

    public static class RequestConfiguration extends HandyEvent.RequestEvent {}


    public static class ReceiveConfigurationSuccess extends HandyEvent.ReceiveSuccessEvent {

        private Configuration mConfiguration;

        public ReceiveConfigurationSuccess(final Configuration configuration) {
            mConfiguration = configuration;
        }

        public Configuration getConfiguration() {
            return mConfiguration;
        }
    }


    public static class ReceiveConfigurationError extends HandyEvent.ReceiveErrorEvent {

        public ReceiveConfigurationError(final DataManager.DataManagerError error) {
            this.error = error;
        }
    }
}
