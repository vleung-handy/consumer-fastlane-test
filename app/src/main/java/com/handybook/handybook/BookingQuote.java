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

public final class BookingQuote extends Observable {
    @SerializedName("id") private int bookingId;
    @SerializedName("hrs") private float hours;

    final int getBookingId() {
        return bookingId;
    }

    final void setBookingId(final int bookingId) {
        this.bookingId = bookingId;
        triggerObservers();
    }

    public float getHours() {
        return hours;
    }

    public void setHours(float hours) {
        this.hours = hours;
        triggerObservers();
    }

    private void triggerObservers() {
        setChanged();
        notifyObservers();
    }

    final String toJson() {
        final Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ")
                .setExclusionStrategies(getExclusionStrategy())
                .registerTypeAdapter(BookingQuote.class, new BookingQuoteSerializer()).create();

        return gson.toJson(this);
    }

    static BookingQuote fromJson(final String json) {
        return new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ").create()
                .fromJson(json, BookingQuote.class);
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

    static final class BookingQuoteSerializer implements JsonSerializer<BookingQuote> {
        @Override
        public final JsonElement serialize(final BookingQuote value, final Type type,
                                           final JsonSerializationContext context) {
            final JsonObject jsonObj = new JsonObject();
            jsonObj.add("id", context.serialize(value.getBookingId()));
            jsonObj.add("hrs", context.serialize(value.getHours()));
            return jsonObj;
        }
    }
}
