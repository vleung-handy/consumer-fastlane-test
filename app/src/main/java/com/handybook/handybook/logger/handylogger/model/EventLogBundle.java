package com.handybook.handybook.logger.handylogger.model;

import com.google.gson.annotations.SerializedName;
import com.handybook.handybook.core.BaseApplication;

import java.util.List;

public class EventLogBundle
{
    @SerializedName("event_bundle_id")
    private String mEventBundleId;
    @SerializedName("events")
    private List<Event> mEvents;
    @SerializedName("super_properties")
    private EventSuperProperties mEventSuperProperties;

    public EventLogBundle(final int userId, final List<Event> events)
    {
        mEventBundleId = createBundleId();
        mEvents = events;
        mEventSuperProperties = new EventSuperProperties(userId);
    }

    private String createBundleId()
    {
        return System.currentTimeMillis() + "+" + BaseApplication.getDeviceId();
    }
}