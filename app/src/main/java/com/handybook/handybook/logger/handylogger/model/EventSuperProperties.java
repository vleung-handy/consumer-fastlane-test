package com.handybook.handybook.logger.handylogger.model;

import com.google.gson.annotations.SerializedName;

public class EventSuperProperties extends EventSuperPropertiesBase
{
    public static final String USER_ID = "user_id";
    @SerializedName(USER_ID)
    private int mUserid;

    public EventSuperProperties(final int userId)
    {
        super();
        mUserid = userId;
    }
}
