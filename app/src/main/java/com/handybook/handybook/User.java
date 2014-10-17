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

    final String getAuthToken() {
        return authToken;
    }

    final void setAuthToken(String authToken) {
        this.authToken = authToken;
        triggerObservers();
    }

    final String getId() {
        return id;
    }

    final void setId(String id) {
        this.id = id;
        triggerObservers();
    }

    final float getCredits() {
        return credits;
    }

    final void setCredits(float credits) {
        this.credits = credits;
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
            return jsonObj;
        }
    }
}
