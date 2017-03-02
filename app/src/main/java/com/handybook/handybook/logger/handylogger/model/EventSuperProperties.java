package com.handybook.handybook.logger.handylogger.model;

import com.google.gson.annotations.SerializedName;

public class EventSuperProperties extends EventSuperPropertiesBase {

    public static final String USER_ID = "user_id";
    @SerializedName(USER_ID)
    private int mUserid;

    public EventSuperProperties(
            int userId,
            String osVersion,
            String appVersion,
            String deviceId,
            String deviceModel,
            String installationId
    ) {
        super(osVersion, appVersion, deviceId, deviceModel, installationId);
        mUserid = userId;
    }
}
