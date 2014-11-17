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
import java.util.Observable;
import java.util.Observer;

public final class BookingPostInfo extends Observable {
    @SerializedName("get_in") private int getInId;
    @SerializedName("get_in_text") private String getInText;
    @SerializedName("extra_message") private String extraMessage;

    final int getGetInId() {
        return getInId;
    }

    final void setGetInId(final int getInId) {
        this.getInId = getInId;
        triggerObservers();
    }

    final String getGetInText() {
        return getInText;
    }

    final void setGetInText(final String getInText) {
        this.getInText = getInText;
        triggerObservers();
    }

    final String getExtraMessage() {
        return extraMessage;
    }

    final void setExtraMessage(final String extraMessage) {
        this.extraMessage = extraMessage;
        triggerObservers();
    }

    private void triggerObservers() {
        setChanged();
        notifyObservers();
    }

    final String toJson() {
        final Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ")
                .setExclusionStrategies(getExclusionStrategy())
                .registerTypeAdapter(BookingPostInfo.class,
                        new BookingPostInfoSerializer()).create();

        return gson.toJson(this);
    }

    static BookingPostInfo fromJson(final String json) {
        return new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ").create()
                .fromJson(json, BookingPostInfo.class);
    }

    static ExclusionStrategy getExclusionStrategy() {
        return new ExclusionStrategy() {
            @Override
            public boolean shouldSkipField(final FieldAttributes f) {
                return false;
            }

            @Override
            public boolean shouldSkipClass(final Class<?> clazz) {
                return clazz.equals(Observer.class);
            }
        };
    }

    static final class BookingPostInfoSerializer implements JsonSerializer<BookingPostInfo> {
        @Override
        public final JsonElement serialize(final BookingPostInfo value, final Type type,
                                           final JsonSerializationContext context) {
            final JsonObject jsonObj = new JsonObject();
            jsonObj.add("get_in", context.serialize(value.getGetInId()));
            jsonObj.add("get_in_text", context.serialize(value.getGetInText()));
            jsonObj.add("extra_message", context.serialize(value.getExtraMessage()));
            return jsonObj;
        }
    }
}
