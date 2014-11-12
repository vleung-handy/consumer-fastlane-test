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
import java.util.Date;
import java.util.Observable;
import java.util.Observer;

public final class BookingTransaction extends Observable {
    @SerializedName("first_name") private String firstName;
    @SerializedName("last_name") private String lastName;
    @SerializedName("address1") private String address1;
    @SerializedName("address2") private String address2;
    @SerializedName("phone") private String phone;
    @SerializedName("hrs") private float hours;
    @SerializedName("start_date") private Date startDate;

    final String getFirstName() {
        return firstName;
    }

    final void setFirstName(final String firstName) {
        this.firstName = firstName;
        triggerObservers();
    }

    final String getLastName() {
        return lastName;
    }

    final void setLastName(final String lastName) {
        this.lastName = lastName;
        triggerObservers();
    }

    final String getAddress1() {
        return address1;
    }

    final void setAddress1(final String address1) {
        this.address1 = address1;
        triggerObservers();
    }

    final String getAddress2() {
        return address2;
    }

    final void setAddress2(final String address2) {
        this.address2 = address2;
        triggerObservers();
    }

    final String getPhone() {
        return phone;
    }

    final void setPhone(final String phone) {
        this.phone = phone;
        triggerObservers();
    }

    final float getHours() {
        return hours;
    }

    final void setHours(final float hours) {
        this.hours = hours;
        triggerObservers();
    }

    final Date getStartDate() {
        return startDate;
    }

    final void setStartDate(final Date startDate) {
        this.startDate = startDate;
        triggerObservers();
    }

    private void triggerObservers() {
        setChanged();
        notifyObservers();
    }

    final String toJson() {
        final Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ")
                .setExclusionStrategies(getExclusionStrategy())
                .registerTypeAdapter(BookingTransaction.class,
                        new BookingTransactionSerializer()).create();

        return gson.toJson(this);
    }

    static BookingTransaction fromJson(final String json) {
        return new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ").create()
                .fromJson(json, BookingTransaction.class);
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

    static final class BookingTransactionSerializer implements JsonSerializer<BookingTransaction> {
        @Override
        public final JsonElement serialize(final BookingTransaction value, final Type type,
                                           final JsonSerializationContext context) {
            final JsonObject jsonObj = new JsonObject();
            jsonObj.add("first_name", context.serialize(value.getFirstName()));
            jsonObj.add("last_name", context.serialize(value.getLastName()));
            jsonObj.add("address1", context.serialize(value.getAddress1()));
            jsonObj.add("address2", context.serialize(value.getAddress2()));
            jsonObj.add("phone", context.serialize(value.getPhone()));
            jsonObj.add("hrs", context.serialize(value.getHours()));
            jsonObj.add("start_date", context.serialize(value.getStartDate()));
            return jsonObj;
        }
    }
}
