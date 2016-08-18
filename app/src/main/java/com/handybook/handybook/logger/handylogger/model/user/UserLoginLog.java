package com.handybook.handybook.logger.handylogger.model.user;


import android.support.annotation.StringDef;

import com.google.gson.annotations.SerializedName;
import com.handybook.handybook.logger.handylogger.model.EventLog;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class UserLoginLog extends EventLog
{
    public static final String AUTH_TYPE_FACEBOOK = "facebook";
    public static final String AUTH_TYPE_EMAIL = "email";
    @Retention(RetentionPolicy.SOURCE)
    @StringDef({
            AUTH_TYPE_EMAIL,
            AUTH_TYPE_FACEBOOK
    })
    public @interface AuthType
    {
    }

    private static final String EVENT_CONTEXT = "login";

    @SerializedName("auth_type")
    private String mAuthType;

    protected UserLoginLog(final String eventType, @AuthType final String authType)
    {
        super(eventType, EVENT_CONTEXT);
        mAuthType = authType;
    }


    public static class UserLoginShownLog extends UserLoginLog
    {
        private static final String EVENT_TYPE = "shown";

        public UserLoginShownLog(@AuthType String authType)
        {
            super(EVENT_TYPE, authType);
        }
    }


    public static class UserLoginSubmittedLog extends UserLoginLog
    {
        private static final String EVENT_TYPE = "submitted";

        @SerializedName("email")
        private String mEmail;

        public UserLoginSubmittedLog(final String email, @AuthType String loginType)
        {
            super(EVENT_TYPE, loginType);
            mEmail = email;
        }
    }

    public static class UserLoginSuccessLog extends UserLoginLog
    {
        private static final String EVENT_TYPE = "success";

        public UserLoginSuccessLog(@AuthType String authType)
        {
            super(EVENT_TYPE, authType);
        }
    }

    public static class UserLoginErrorLog extends UserLoginLog
    {
        private static final String EVENT_TYPE = "error";

        @SerializedName("error_message")
        private final String mErrorMessage;

        public UserLoginErrorLog(@AuthType String authType, String errorMessage)
        {
            super(EVENT_TYPE, authType);
            mErrorMessage = errorMessage;
        }
    }
}
