package com.handybook.handybook.logger.handylogger.model;

import com.google.gson.annotations.SerializedName;

public abstract class OnboardingLog extends EventLog
{
    private static final String EVENT_CONTEXT = "onboarding";

    public OnboardingLog(final String eventType)
    {
        super(eventType, EVENT_CONTEXT);
    }

    public static class ZipSubmittedLog extends OnboardingLog
    {
        private static final String EVENT_TYPE = "zip_submitted";

        @SerializedName("zip")
        private final String mZip;

        @SerializedName("device_locale")
        private final String mDeviceLocale;

        public ZipSubmittedLog(final String zip, final String deviceLocale)
        {
            super(EVENT_TYPE);
            mZip = zip;
            mDeviceLocale = deviceLocale;
        }
    }


    public static class EmailCollectedLog extends OnboardingLog
    {
        private static final String EVENT_TYPE = "email_collected";

        @SerializedName("email_address")
        private final String mEmailAddress;

        public EmailCollectedLog(final String emailAddress)
        {
            super(EVENT_TYPE);
            mEmailAddress = emailAddress;
        }
    }


    public static class ZipNotSupportedLog extends OnboardingLog
    {
        private static final String EVENT_TYPE = "zip_not_supported";

        @SerializedName("zip")
        private final String mZip;

        public ZipNotSupportedLog(final String zip)
        {
            super(EVENT_TYPE);
            mZip = zip;
        }
    }
}
