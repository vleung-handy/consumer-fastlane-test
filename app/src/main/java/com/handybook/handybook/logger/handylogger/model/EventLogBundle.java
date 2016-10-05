package com.handybook.handybook.logger.handylogger.model;

import android.support.annotation.NonNull;

import com.google.gson.annotations.SerializedName;
import com.handybook.handybook.core.BaseApplication;

import java.util.List;

public class EventLogBundle
{
    public static final String EVENT_BUNDLE_ID_KEY = "event_bundle_id";

    @SerializedName(EVENT_BUNDLE_ID_KEY)
    private String mEventBundleId;
    @SerializedName("events")
    private List<Event> mEvents;
    @SerializedName("super_properties")
    private EventSuperProperties mEventSuperProperties;

    public EventLogBundle(final int userId, @NonNull final List<Event> events)
    {
        mEventBundleId = createBundleId();
        mEvents = events;
        mEventSuperProperties = new EventSuperProperties(userId);
    }

    public String getEventBundleId() {
        return mEventBundleId;
    }

    public void addEvent(Event event) {
        mEvents.add(event);
    }

    public int size() {
        return mEvents.size();
    }

    private String createBundleId()
    {
        return System.currentTimeMillis() + "+" + BaseApplication.getDeviceId();
    }
}
