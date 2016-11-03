package com.handybook.handybook.logger.handylogger.model;

import android.support.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class EventLogBundle
{
    public static final String KEY_EVENT_BUNDLE_ID = "event_bundle_id";

    @SerializedName(KEY_EVENT_BUNDLE_ID)
    private String mEventBundleId;
    @SerializedName("events")
    private List<Event> mEvents;
    @SerializedName("super_properties")
    private EventSuperPropertiesBase mEventSuperProperties;


    /**
     * If user id greater then 0, then user_id will be part of super properties
     *
     * @param userId
     * @param events
     */
    public EventLogBundle(
            final int userId,
            @NonNull final List<Event> events,
            @NonNull final String osVersion,
            @NonNull final String appVersion,
            @NonNull final String deviceId,
            @NonNull final String deviceModel,
            @NonNull final String installationId
    )
    {
        mEventBundleId = System.currentTimeMillis() + "+" + deviceId;
        mEvents = events;

        if (userId > 0)
        {
            mEventSuperProperties = new EventSuperProperties(
                    userId, osVersion, appVersion, deviceId, deviceModel, installationId);
        }
        else
        {
            mEventSuperProperties = new EventSuperPropertiesBase(
                    osVersion, appVersion, deviceId, deviceModel, installationId);
        }
    }

    public String getEventBundleId()
    {
        return mEventBundleId;
    }

    public void addEvent(Event event)
    {
        mEvents.add(event);
    }

    public int size()
    {
        return mEvents.size();
    }
}
