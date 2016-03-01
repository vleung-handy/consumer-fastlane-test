package com.handybook.handybook.module.configuration.event;

import com.handybook.handybook.data.DataManager;
import com.handybook.handybook.event.HandyEvent;
import com.handybook.handybook.module.configuration.model.Configuration;

public class ConfigurationEvent
{
    public static class RequestConfiguration extends HandyEvent.RequestEvent {}


    public static class ReceiveConfigurationSuccess extends HandyEvent.ReceiveSuccessEvent
    {
        private Configuration mConfiguration;

        public ReceiveConfigurationSuccess(final Configuration configuration)
        {
            mConfiguration = configuration;
        }

        public Configuration getConfiguration()
        {
            return mConfiguration;
        }
    }


    public static class ReceiveConfigurationError extends HandyEvent.ReceiveErrorEvent
    {
        public ReceiveConfigurationError(final DataManager.DataManagerError error)
        {
            this.error = error;
        }
    }


    public static class RefreshConfiguration {}
}
