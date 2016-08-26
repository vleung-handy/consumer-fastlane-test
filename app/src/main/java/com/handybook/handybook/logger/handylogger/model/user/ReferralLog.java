package com.handybook.handybook.logger.handylogger.model.user;

import com.google.gson.annotations.SerializedName;
import com.handybook.handybook.logger.handylogger.model.EventLog;

public abstract class ReferralLog extends EventLog
{
    private static final String EVENT_CONTEXT = "referral";

    public ReferralLog(final String eventType)
    {
        super(eventType, EVENT_CONTEXT);
    }


    public static class ReferralOpenLog extends ReferralLog
    {
        private static final String EVENT_TYPE = "open";

        public ReferralOpenLog()
        {
            super(EVENT_TYPE);
        }
    }


    public static class ShareButtonTapped extends ReferralLog
    {
        private static final String EVENT_TYPE = "share_button_tapped";
        @SerializedName("referral_medium")
        private String mReferralMedium;
        @SerializedName("referral_identifier")
        private String mReferralIdentifier;

        public ShareButtonTapped(
                final String referralMedium,
                final String referralIdentifier
        )
        {
            super(EVENT_TYPE);
            mReferralMedium = referralMedium;
            mReferralIdentifier = referralIdentifier;
        }
    }
}
