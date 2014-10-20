package com.handybook.handybook;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.annotations.SerializedName;

import java.lang.reflect.Type;
import java.util.Observable;

public final class User extends Observable {
    @SerializedName("auth_token") private String authToken;
    @SerializedName("id") private String id;
    @SerializedName("credits") private float credits;
    @SerializedName("first_name") private String firstName;
    @SerializedName("last_name") private String lastName;
    @SerializedName("email") private String email;
    @SerializedName("phone_country_prefix") private String phonePrefix;
    @SerializedName("phone") private String phone;
    @SerializedName("currency_char") private String currencyChar;
    @SerializedName("currency_suffix") private String currencySuffix;

    final String getAuthToken() {
        return authToken;
    }

    final void setAuthToken(final String authToken) {
        this.authToken = authToken;
        triggerObservers();
    }

    final String getId() {
        return id;
    }

    final void setId(final String id) {
        this.id = id;
        triggerObservers();
    }

    final float getCredits() {
        return credits;
    }

    final void setCredits(final float credits) {
        this.credits = credits;
        triggerObservers();
    }

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

    final String getEmail() {
        return email;
    }

    final void setEmail(final String email) {
        this.email = email;
        triggerObservers();
    }

    final String getPhonePrefix() {
        return phonePrefix;
    }

    final void setPhonePrefix(final String phonePrefix) {
        this.phonePrefix = phonePrefix;
        triggerObservers();
    }

    final String getPhone() {
        return phone;
    }

    final void setPhone(final String phone) {
        this.phone = phone;
        triggerObservers();
    }

    final String getCurrencyChar() {
        return currencyChar;
    }

    final void setCurrencyChar(final String currencyChar) {
        this.currencyChar = currencyChar;
        triggerObservers();
    }

    final String getCurrencySuffix() {
        return currencySuffix;
    }

    final void setCurrencySuffix(final String currencySuffix) {
        this.currencySuffix = currencySuffix;
        triggerObservers();
    }

    private void triggerObservers() {
        setChanged();
        notifyObservers();
    }

    static final class UserSerializer implements JsonSerializer<User> {
        @Override
        public final JsonElement serialize(final User value, final Type type,
                                     final JsonSerializationContext context) {
            final JsonObject jsonObj = new JsonObject();
            jsonObj.add("auth_token", context.serialize(value.getAuthToken()));
            jsonObj.add("id", context.serialize(value.getId()));
            jsonObj.add("credits", context.serialize(value.getCredits()));
            jsonObj.add("first_name", context.serialize(value.getFirstName()));
            jsonObj.add("last_name", context.serialize(value.getLastName()));
            jsonObj.add("email", context.serialize(value.getEmail()));
            jsonObj.add("phone_country_prefix", context.serialize(value.getPhonePrefix()));
            jsonObj.add("phone", context.serialize(value.getPhone()));
            jsonObj.add("currency_char", context.serialize(value.getCurrencyChar()));
            jsonObj.add("currency_suffix", context.serialize(value.getCurrencySuffix()));
            return jsonObj;
        }
    }
}
