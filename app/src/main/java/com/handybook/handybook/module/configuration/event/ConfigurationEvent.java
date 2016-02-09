package com.handybook.handybook.module.configuration.event;

import com.handybook.handybook.data.DataManager;
import com.handybook.handybook.event.HandyEvent;
import com.handybook.handybook.module.configuration.model.Configuration;

public class ConfigurationEvent
{
    public static class RequestConfiguration extends HandyEvent.RequestEvent {}


    public static class ReceiveConfigurationSuccess extends HandyEvent.ReceiveSuccessEvent
    {
        private Configuration mResponse;

        public ReceiveConfigurationSuccess(final Configuration response)
        {
            mResponse = response;
        }

        public Configuration getResponse()
        {
            return mResponse;
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
