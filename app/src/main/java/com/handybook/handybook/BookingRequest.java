package com.handybook.handybook;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.annotations.SerializedName;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Observable;
import java.util.Observer;

public final class BookingRequest extends Observable {
    @SerializedName("service_id") private int serviceId;
    @SerializedName("zipcode") private String zipCode;
    @SerializedName("options") private HashMap<String, String> options;

    final int getServiceId() {
        return serviceId;
    }

    final void setServiceId(final int serviceId) {
        this.serviceId = serviceId;
        triggerObservers();
    }

    final String getZipCode() {
        return zipCode;
    }

    final void setZipCode(final String zipCode) {
        this.zipCode = zipCode;
        triggerObservers();
    }

    final HashMap<String, String> getOptions() {
        if (options == null) options = new HashMap<>();
        return options;
    }

    final void setOptions(final HashMap<String, String> options) {
        this.options = options;
        triggerObservers();
    }

    private void triggerObservers() {
        setChanged();
        notifyObservers();
    }

    final String toJson() {
        final Gson gson = new GsonBuilder().setExclusionStrategies(new ExclusionStrategy() {
            @Override
            public boolean shouldSkipField(final FieldAttributes f) {
                return false;
            }

            @Override
            public boolean shouldSkipClass(final Class<?> clazz) {
                return clazz.equals(Observer.class);
            }
        }).registerTypeAdapter(BookingRequest.class, new BookingSerializer()).create();
        return gson.toJson(this);
    }

    static final class BookingSerializer implements JsonSerializer<BookingRequest> {
        @Override
        public final JsonElement serialize(final BookingRequest value, final Type type,
                                           final JsonSerializationContext context) {
            final JsonObject jsonObj = new JsonObject();
            jsonObj.add("service_id", context.serialize(value.getServiceId()));
            jsonObj.add("zipcode", context.serialize(value.getZipCode()));
            jsonObj.add("options", context.serialize(value.getOptions()));
            return jsonObj;
        }
    }
}
